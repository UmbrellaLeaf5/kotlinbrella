package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.Mode
import io.github.umbrellaleaf5.kotlinbrella.error.ApiException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorViolation
import io.github.umbrellaleaf5.kotlinbrella.error.SimpleErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.net.URI
import java.util.UUID

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class KotlinbrellaErrorAdvice(
  // params:
  private val properties: KotlinbrellaWebProperties,

  // mappers:
  private val violationMapper: ErrorViolationMapper,
) {

  private val logger = LoggerFactory.getLogger(javaClass)

  // MARK: Render known client exceptions
  // --------------------------------------------------

  @ExceptionHandler(ApiException::class)
  fun apiException(
    exception: ApiException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> =
    problem(
      HttpStatusCode.valueOf(exception.status),
      exception.code,
      if (exposeDiagnostics()) exception.diagnosticDetail else exception.publicDetail,
      request,
    )

  // MARK: Render validation failures
  // --------------------------------------------------

  /**
   * Преобразует ошибки полей и ошибки уровня объекта в единый ответ проверки запроса.
   */
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun invalidArgument(
    exception: MethodArgumentNotValidException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> {
    val violations = exception.bindingResult.fieldErrors.map(violationMapper::toFieldViolation) +
      exception.bindingResult.globalErrors.map(violationMapper::toGlobalViolation)

    return problem(
      HttpStatus.BAD_REQUEST,
      ErrorCode.BAD_REQUEST,
      Constants.ErrorDescription.VALIDATION_FAILED,
      request,
      violations,
    )
  }

  // --------------------------------------------------

  @ExceptionHandler(ConstraintViolationException::class)
  fun constraintViolation(
    exception: ConstraintViolationException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> = problem(
    HttpStatus.BAD_REQUEST,
    ErrorCode.BAD_REQUEST,
    Constants.ErrorDescription.VALIDATION_FAILED,
    request,
    exception.constraintViolations.map(violationMapper::toConstraintViolation),
  )

  // MARK: Render malformed or missing request values
  // --------------------------------------------------

  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun unreadableMessage(
    exception: HttpMessageNotReadableException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> {
    val field = (exception.cause as? InvalidFormatException)
      ?.path?.firstOrNull()?.fieldName
    val detail = if (field != null && exposeDiagnostics())
      "${Constants.ErrorDescription.INVALID_VALUE} for field '$field'"
    else
      Constants.ErrorDescription.INVALID_REQUEST

    return problem(
      HttpStatus.BAD_REQUEST,
      ErrorCode.BAD_REQUEST,
      detail,
      request,
    )
  }

  // --------------------------------------------------

  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun typeMismatch(
    exception: MethodArgumentTypeMismatchException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> {
    val detail = if (exposeDiagnostics())
      "${Constants.ErrorDescription.INVALID_VALUE} for parameter '${exception.name}'"
    else
      Constants.ErrorDescription.INVALID_REQUEST

    return problem(
      HttpStatus.BAD_REQUEST,
      ErrorCode.BAD_REQUEST,
      detail,
      request,
    )
  }

  // --------------------------------------------------

  @ExceptionHandler(
    MissingServletRequestParameterException::class,
    MissingRequestHeaderException::class,
  )
  fun missingValue(
    exception: MissingRequestValueException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> {
    val name = when (exception) {
      is MissingServletRequestParameterException -> exception.parameterName
      is MissingRequestHeaderException -> exception.headerName
      else -> null
    }
    val detail = if (name != null && exposeDiagnostics())
      "${Constants.ErrorDescription.MISSING_VALUE}: '$name'"
    else
      Constants.ErrorDescription.INVALID_REQUEST

    return problem(
      HttpStatus.BAD_REQUEST,
      ErrorCode.BAD_REQUEST,
      detail,
      request,
    )
  }

  // MARK: Preserve HTTP status semantics
  // --------------------------------------------------

  @ExceptionHandler(NoResourceFoundException::class, NoHandlerFoundException::class)
  fun notFound(request: HttpServletRequest): ResponseEntity<Any> =
    problem(
      HttpStatus.NOT_FOUND,
      ErrorCode.NOT_FOUND,
      Constants.ErrorDescription.RESOURCE_NOT_FOUND,
      request,
    )

  // --------------------------------------------------

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  fun methodNotAllowed(request: HttpServletRequest): ResponseEntity<Any> =
    problem(
      HttpStatus.METHOD_NOT_ALLOWED,
      ErrorCode.METHOD_NOT_ALLOWED,
      Constants.ErrorDescription.METHOD_NOT_ALLOWED,
      request,
    )

  // --------------------------------------------------

  @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
  fun unsupportedMediaType(
    exception: HttpMediaTypeNotSupportedException,
    request: HttpServletRequest,
  ): ResponseEntity<Any> {
    val detail = if (exception.contentType != null)
      "${exception.contentType} is not supported"
    else
      Constants.ErrorDescription.MISSING_CONTENT_TYPE

    return problem(
      HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      ErrorCode.UNSUPPORTED_MEDIA_TYPE,
      detail,
      request,
    )
  }

  // MARK: Hide unexpected server errors
  // --------------------------------------------------

  @ExceptionHandler(Exception::class)
  fun unexpected(
    exception: Exception,
    request: HttpServletRequest,
  ): ResponseEntity<Any> =
    problem(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCode.INTERNAL_ERROR,
      Constants.ErrorDescription.INTERNAL_ERROR,
      request,
      cause = exception,
    )

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun exposeDiagnostics(): Boolean = properties.mode == Mode.DEV

  // --------------------------------------------------

  private fun verbose(): Boolean = exposeDiagnostics()

  // --------------------------------------------------

  private fun joinViolations(violations: List<ErrorViolation>): String =
    violations.joinToString("; ") {
      if (it.field == null) it.message else "${it.field}: ${it.message}"
    }

  // --------------------------------------------------

  private fun logRecord(
    status: HttpStatusCode,
    code: String,
    detail: String,
    request: HttpServletRequest,
    cause: Throwable? = null,
  ) {
    if (cause != null || status.is5xxServerError) {
      logger.error(Constants.Web.UNKNOWN_FAILURE_LOG, cause)
      return
    }

    if (!status.is4xxClientError) return

    if (verbose())
      logger.info(Constants.Web.EXPECTED_FAILURE_DETAIL_LOG, code, detail, request.requestURI)

    else
      logger.info(Constants.Web.EXPECTED_FAILURE_LOG, code)
  }

  // --------------------------------------------------

  /**
   * Формирует безопасный ответ об ошибке в выбранной форме с кодом и идентификатором запроса.
   */
  private fun problem(
    status: HttpStatusCode,
    code: String,
    detail: String,
    request: HttpServletRequest,
    violations: List<ErrorViolation> = emptyList(),
    cause: Throwable? = null,
  ): ResponseEntity<Any> {
    logRecord(status, code, detail, request, cause)

    val traceId = request.getAttribute(Constants.Web.TRACE_ID_ATTRIBUTE)
      ?: UUID.randomUUID().toString()

    if (properties.errorShape == ErrorShape.SIMPLE) {
      val message = if (violations.isNotEmpty()) joinViolations(violations) else detail
      val simple = SimpleErrorResponse(ErrorCode.title(code), message)

      return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(simple)
    }

    val body = ProblemDetail.forStatusAndDetail(status, detail)
    body.instance = URI.create(request.requestURI)
    body.setProperty(Constants.Web.TYPE_KEY, Constants.Web.ABOUT_BLANK)
    body.setProperty(Constants.Web.CODE_KEY, code)
    body.setProperty(Constants.Web.TRACE_ID_KEY, traceId)

    if (violations.isNotEmpty())
      body.setProperty(
        Constants.Web.VIOLATIONS_KEY,
        violations.map(violationMapper::toProperties),
      )

    return ResponseEntity.status(status).body(body)
  }

}

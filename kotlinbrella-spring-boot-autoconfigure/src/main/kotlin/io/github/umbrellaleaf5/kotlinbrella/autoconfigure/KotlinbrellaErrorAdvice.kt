package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.ApiException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorViolation
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.springframework.web.servlet.NoHandlerFoundException
import java.net.URI
import java.util.UUID

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class KotlinbrellaErrorAdvice(
  // params:
  private val properties: KotlinbrellaWebProperties,
  private val environment: Environment,
) {

  private val logger = LoggerFactory.getLogger(javaClass)

  // MARK: Render known client exceptions
  // --------------------------------------------------

  @ExceptionHandler(ApiException::class)
  fun apiException(exception: ApiException, request: HttpServletRequest): ResponseEntity<ProblemDetail> =
    problem(
      HttpStatusCode.valueOf(exception.status),
      exception.code,
      if (exposeDiagnostics()) exception.diagnosticDetail else exception.publicDetail,
      request,
    )

  // MARK: Render validation failures
  // --------------------------------------------------

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun invalidArgument(
    exception: MethodArgumentNotValidException,
    request: HttpServletRequest,
  ): ResponseEntity<ProblemDetail> {
    val violations = exception.bindingResult.fieldErrors.map {
      ErrorViolation(it.field, it.defaultMessage ?: Constants.ErrorDescription.INVALID_VALUE,
        ErrorCode.BAD_REQUEST)
    } + exception.bindingResult.globalErrors.map {
      ErrorViolation(null, it.defaultMessage ?: Constants.ErrorDescription.INVALID_VALUE,
        if (it.code == Constants.Validation.AT_LEAST_ONE_PRESENT_NAME)
          ErrorCode.AT_LEAST_ONE_PRESENT else ErrorCode.BAD_REQUEST)
    }

    return problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
      Constants.ErrorDescription.VALIDATION_FAILED, request,
      violations)
  }

  // --------------------------------------------------

  @ExceptionHandler(ConstraintViolationException::class)
  fun constraintViolation(
    exception: ConstraintViolationException,
    request: HttpServletRequest,
  ): ResponseEntity<ProblemDetail> = problem(
    HttpStatus.BAD_REQUEST,
    ErrorCode.BAD_REQUEST,
    Constants.ErrorDescription.VALIDATION_FAILED,
    request,
    exception.constraintViolations.map {
      ErrorViolation(it.propertyPath.toString(), it.message,
        if (it.constraintDescriptor.annotation.annotationClass.java.name ==
          Constants.Validation.AT_LEAST_ONE_PRESENT_CLASS)
          ErrorCode.AT_LEAST_ONE_PRESENT else ErrorCode.BAD_REQUEST)
    },
  )

  // MARK: Render malformed or missing request values
  // --------------------------------------------------

  @ExceptionHandler(
    HttpMessageNotReadableException::class,
    MethodArgumentTypeMismatchException::class,
    MissingServletRequestParameterException::class,
    MissingRequestHeaderException::class,
  )
  fun badRequest(request: HttpServletRequest): ResponseEntity<ProblemDetail> =
    problem(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
      Constants.ErrorDescription.INVALID_REQUEST, request)

  // MARK: Preserve HTTP status semantics
  // --------------------------------------------------

  @ExceptionHandler(NoResourceFoundException::class, NoHandlerFoundException::class)
  fun notFound(request: HttpServletRequest): ResponseEntity<ProblemDetail> =
    problem(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,
      Constants.ErrorDescription.RESOURCE_NOT_FOUND, request)

  // --------------------------------------------------

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  fun methodNotAllowed(request: HttpServletRequest): ResponseEntity<ProblemDetail> =
    problem(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED,
      Constants.ErrorDescription.METHOD_NOT_ALLOWED, request)

  // --------------------------------------------------

  @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
  fun unsupportedMediaType(request: HttpServletRequest): ResponseEntity<ProblemDetail> =
    problem(
      HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      ErrorCode.UNSUPPORTED_MEDIA_TYPE,
      Constants.ErrorDescription.UNSUPPORTED_MEDIA_TYPE,
      request,
    )

  // MARK: Hide unexpected server errors
  // --------------------------------------------------

  @ExceptionHandler(Exception::class)
  fun unexpected(
    exception: Exception,
    request: HttpServletRequest,
  ): ResponseEntity<ProblemDetail> {
    logger.error("Unexpected request failure", exception)

    return problem(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ErrorCode.INTERNAL_ERROR,
      Constants.ErrorDescription.INTERNAL_ERROR,
      request,
    )
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun exposeDiagnostics(): Boolean = properties.exposeDebugDetails ||
    properties.diagnosticProfiles.any { environment.acceptsProfiles(Profiles.of(it)) }

  // --------------------------------------------------

  private fun problem(
    status: HttpStatusCode,
    code: String,
    detail: String,
    request: HttpServletRequest,
    violations: List<ErrorViolation> = emptyList(),
  ): ResponseEntity<ProblemDetail> {
    if (properties.logExpected4xx && status.is4xxClientError)
      logger.info("Expected client failure: {}", code)

    val body = ProblemDetail.forStatusAndDetail(status, detail)
    body.instance = URI.create(request.requestURI)
    body.setProperty(Constants.Web.TYPE_KEY, Constants.Web.ABOUT_BLANK)
    body.setProperty(Constants.Web.CODE_KEY, code)
    body.setProperty(Constants.Web.TRACE_ID_KEY, request.getAttribute(Constants.Web.TRACE_ID_ATTRIBUTE)
      ?: UUID.randomUUID().toString())

    if (violations.isNotEmpty())
      body.setProperty(Constants.Web.VIOLATIONS_KEY, violations)

    return ResponseEntity.status(status).body(body)
  }

}

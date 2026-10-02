package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import ch.qos.logback.classic.Logger
import ch.qos.logback.core.read.ListAppender
import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.LogLevel
import io.github.umbrellaleaf5.kotlinbrella.error.LegacyErrorResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.env.MockEnvironment
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.UUID

class KotlinbrellaErrorAdviceTest {

  // MARK: Keep private diagnostics out of production responses
  // --------------------------------------------------

  @Test
  fun redactsDiagnosticDetailsByDefault() {
    val request = MockHttpServletRequest("GET", "/resource")
    val advice = KotlinbrellaErrorAdvice(
      KotlinbrellaWebProperties(),
      MockEnvironment(),
      ErrorViolationMapper(),
    )
    val result = advice.apiException(BadRequestException("secret", "safe"), request)
    val body = result.body as ProblemDetail

    assertEquals(400, result.statusCode.value())
    assertEquals("safe", body.detail)
    assertEquals("BAD_REQUEST", body.properties?.get("code"))
    assertEquals("/resource", body.instance.toString())
  }

  // --------------------------------------------------

  @Test
  fun explicitDiagnosticProfileEnablesDetails() {
    val properties = KotlinbrellaWebProperties().apply {
      diagnosticProfiles = setOf("debug")
    }
    val environment = MockEnvironment().withProperty("spring.profiles.active", "debug")
    val advice = KotlinbrellaErrorAdvice(properties, environment, ErrorViolationMapper())

    val body = advice.apiException(BadRequestException("secret", "safe"),
      MockHttpServletRequest()).body as ProblemDetail

    assertEquals("secret", body.detail)
  }

  // --------------------------------------------------

  @Test
  fun unknownFailuresDoNotExposeExceptionText() {
    val logger = LoggerFactory.getLogger(KotlinbrellaErrorAdvice::class.java) as Logger
    val appender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
    appender.start()
    logger.addAppender(appender)

    val advice = KotlinbrellaErrorAdvice(
      KotlinbrellaWebProperties(),
      MockEnvironment(),
      ErrorViolationMapper(),
    )

    try {
      val result = advice.unexpected(IllegalStateException("database password"),
        MockHttpServletRequest())

      assertEquals(500, result.statusCode.value())
      assertFalse(result.body.toString().contains("database password"))
      assertEquals(1, appender.list.size)
      assertTrue(appender.list.single().throwableProxy != null)
    }

    finally {
      logger.detachAppender(appender)
      appender.stop()
    }
  }

  // --------------------------------------------------

  @Test
  fun rendersFieldViolations() {
    val method = WebFixtureController::class.java.getMethod("probe", String::class.java)
    val errors = BeanPropertyBindingResult(WebFixtureInput(null), "request")
    errors.rejectValue("value", "NOT_BLANK", "Field required")
    val exception = MethodArgumentNotValidException(MethodParameter(method, 0), errors)
    val result = KotlinbrellaErrorAdvice(
      KotlinbrellaWebProperties(),
      MockEnvironment(),
      ErrorViolationMapper(),
    )
      .invalidArgument(exception, MockHttpServletRequest())

    assertEquals(400, result.statusCode.value())

    val violations = (result.body as ProblemDetail)
      .properties?.get(Constants.Web.VIOLATIONS_KEY) as List<*>

    val fieldViolation = violations.first() as Map<*, *>

    assertEquals("value", fieldViolation[Constants.ApiSpec.FIELD_KEY])
  }

  // --------------------------------------------------

  @Test
  fun preservesResourceAndValidationStatuses() {
    val advice = KotlinbrellaErrorAdvice(
      KotlinbrellaWebProperties(),
      MockEnvironment(),
      ErrorViolationMapper(),
    )
    val request = MockHttpServletRequest()

    assertEquals(404, advice.notFound(request).statusCode.value())
    assertEquals(400, advice.constraintViolation(ConstraintViolationException(emptySet()),
      request).statusCode.value())
    assertEquals(400, advice.unreadableMessage(
      HttpMessageNotReadableException("broken", MockHttpInputMessage(ByteArray(0))), request).statusCode.value())
    assertEquals(400, advice.typeMismatch(
      probeMismatch(), request).statusCode.value())
    assertEquals(400, advice.missingValue(
      MissingServletRequestParameterException("value", "String"), request)
      .statusCode.value())
    assertEquals(405, advice.methodNotAllowed(request).statusCode.value())

    val unsupported = advice.unsupportedMediaType(
      HttpMediaTypeNotSupportedException(
        MediaType.APPLICATION_XML,
        listOf(MediaType.APPLICATION_JSON),
      ),
      request,
    )

    assertEquals(415, unsupported.statusCode.value())
    assertTrue((unsupported.body as ProblemDetail).detail.orEmpty().contains("application/xml"))
  }

  // --------------------------------------------------

  @Test
  fun preservesPatchViolationCode() {
    val violations = Validation.buildDefaultValidatorFactory().validator
      .validate(WebFixturePatchInput())
    val result = KotlinbrellaErrorAdvice(
      KotlinbrellaWebProperties(),
      MockEnvironment(),
      ErrorViolationMapper(),
    )
      .constraintViolation(ConstraintViolationException(violations), MockHttpServletRequest())
    val details = (result.body as ProblemDetail)
      .properties?.get(Constants.Web.VIOLATIONS_KEY) as List<*>

    val globalViolation = details.single() as Map<*, *>

    assertEquals(ErrorCode.AT_LEAST_ONE_PRESENT, globalViolation[Constants.Web.CODE_KEY])
  }

  // --------------------------------------------------

  @Test
  fun rendersLegacyShapeWithoutProblemFields() {
    val properties = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.LEGACY
    }
    val advice = KotlinbrellaErrorAdvice(properties, MockEnvironment(), ErrorViolationMapper())
    val request = MockHttpServletRequest("GET", "/resource")

    val single = advice.apiException(BadRequestException("secret", "safe"),
      request).body as LegacyErrorResponse

    assertEquals("Bad Request", single.error)
    assertEquals("safe", single.message)

    val method = WebFixtureController::class.java.getMethod("probe", String::class.java)
    val errors = BeanPropertyBindingResult(WebFixtureInput(null), "request")
    errors.rejectValue("value", "NOT_BLANK", "Field required")
    val joined = advice.invalidArgument(MethodArgumentNotValidException(
      MethodParameter(method, 0), errors), request).body as LegacyErrorResponse

    assertEquals("Bad Request", joined.error)
    assertEquals("value: Field required", joined.message)
  }

  // --------------------------------------------------

  @Test
  fun logLevelThresholdSkipsExpectedFailures() {
    val logger = LoggerFactory.getLogger(KotlinbrellaErrorAdvice::class.java) as Logger
    val appender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
    appender.start()
    logger.addAppender(appender)

    val properties = KotlinbrellaWebProperties().apply {
      logExpected4xx = true
      logLevel = LogLevel.ERROR
    }
    val advice = KotlinbrellaErrorAdvice(properties, MockEnvironment(), ErrorViolationMapper())

    try {
      advice.apiException(BadRequestException.unified("bad"),
        MockHttpServletRequest())

      assertTrue(appender.list.isEmpty())

      advice.unexpected(IllegalStateException("failure"), MockHttpServletRequest())

      assertEquals(1, appender.list.size)
      assertTrue(appender.list.single().throwableProxy != null)
    }

    finally {
      logger.detachAppender(appender)
      appender.stop()
    }
  }

  // --------------------------------------------------

  @Test
  fun verboseRecordsIncludeDetailsWhileMinimalKeepsCode() {
    val logger = LoggerFactory.getLogger(KotlinbrellaErrorAdvice::class.java) as Logger
    val appender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
    appender.start()
    logger.addAppender(appender)

    val verbose = KotlinbrellaWebProperties().apply {
      logExpected4xx = true
      exposeDebugDetails = true
    }
    val verboseAdvice = KotlinbrellaErrorAdvice(verbose, MockEnvironment(), ErrorViolationMapper())

    try {
      verboseAdvice.apiException(BadRequestException("secret", "safe"),
        MockHttpServletRequest("GET", "/resource"))

      assertTrue(appender.list.single().formattedMessage.contains("secret"))

      appender.list.clear()

      val minimalAdvice = KotlinbrellaErrorAdvice(
        KotlinbrellaWebProperties().apply { logExpected4xx = true },
        MockEnvironment(),
        ErrorViolationMapper(),
      )
      minimalAdvice.apiException(BadRequestException("secret", "safe"),
        MockHttpServletRequest("GET", "/resource"))

      assertEquals("Expected client failure: BAD_REQUEST", appender.list.single().formattedMessage)
    }

    finally {
      logger.detachAppender(appender)
      appender.stop()
    }
  }

  // --------------------------------------------------

  @Test
  fun diagnosticDetailsNameFieldsWithoutRejectedValues() {
    val properties = KotlinbrellaWebProperties().apply {
      exposeDebugDetails = true
    }
    val advice = KotlinbrellaErrorAdvice(properties, MockEnvironment(), ErrorViolationMapper())
    val request = MockHttpServletRequest()

    val unreadable = advice.unreadableMessage(
      HttpMessageNotReadableException("broken", MockHttpInputMessage(ByteArray(0))), request).body as ProblemDetail

    assertEquals("Invalid request", unreadable.detail)

    val mismatch = advice.typeMismatch(
      probeMismatch(), request).body as ProblemDetail

    assertEquals("Invalid value for parameter 'value'", mismatch.detail)

    val missing = advice.missingValue(
      MissingServletRequestParameterException("value", "String"), request)
      .body as ProblemDetail

    assertEquals("Missing required value: 'value'", missing.detail)
  }

  // --------------------------------------------------

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun probeMismatch(): MethodArgumentTypeMismatchException {
    val method = WebFixtureController::class.java.getMethod("probe", String::class.java)

    return MethodArgumentTypeMismatchException("x", String::class.java, "value",
      MethodParameter(method, 0), IllegalArgumentException())
  }

  // --------------------------------------------------

  @Test
  fun rejectsUnsafeIncomingRequestId() {
    val request = MockHttpServletRequest()
    request.addHeader(Constants.Web.REQUEST_ID_HEADER, "invalid\r\nheader")
    val response = MockHttpServletResponse()
    val filter = KotlinbrellaRequestIdFilter(KotlinbrellaWebProperties())

    filter.doFilter(request, response, MockFilterChain())

    val requestId = response.getHeader(Constants.Web.REQUEST_ID_HEADER)

    assertTrue(runCatching { UUID.fromString(requestId) }.isSuccess)
    assertEquals(null, MDC.get(Constants.Web.TRACE_ID_KEY))
  }

  // --------------------------------------------------

  @Test
  fun propagatesValidRequestId() {
    val request = MockHttpServletRequest()
    request.addHeader(Constants.Web.REQUEST_ID_HEADER, "safe-id")
    val response = MockHttpServletResponse()

    KotlinbrellaRequestIdFilter(KotlinbrellaWebProperties())
      .doFilter(request, response, MockFilterChain())

    assertEquals("safe-id", response.getHeader(Constants.Web.REQUEST_ID_HEADER))
    assertEquals("safe-id", request.getAttribute(Constants.Web.TRACE_ID_ATTRIBUTE))
  }

}

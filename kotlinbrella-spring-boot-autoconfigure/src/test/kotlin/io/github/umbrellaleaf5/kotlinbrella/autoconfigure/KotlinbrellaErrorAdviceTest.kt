package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorViolation
import ch.qos.logback.classic.Logger
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.mock.env.MockEnvironment
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockFilterChain
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import jakarta.validation.ConstraintViolationException

class KotlinbrellaErrorAdviceTest {

  // MARK: Keep private diagnostics out of production responses
  // --------------------------------------------------

  @Test
  fun redactsDiagnosticDetailsByDefault() {
    val request = MockHttpServletRequest("GET", "/resource")
    val advice = KotlinbrellaErrorAdvice(KotlinbrellaWebProperties(), MockEnvironment())
    val result = advice.apiException(BadRequestException("secret", "safe"), request)

    assertEquals(400, result.statusCode.value())
    assertEquals("safe", result.body?.detail)
    assertEquals("BAD_REQUEST", result.body?.properties?.get("code"))
    assertEquals("/resource", result.body?.instance.toString())
  }

  // --------------------------------------------------

  @Test
  fun explicitDiagnosticProfileEnablesDetails() {
    val properties = KotlinbrellaWebProperties().apply {
      diagnosticProfiles = setOf("debug")
    }
    val environment = MockEnvironment().withProperty("spring.profiles.active", "debug")
    val advice = KotlinbrellaErrorAdvice(properties, environment)

    assertEquals(
      "secret",
      advice.apiException(BadRequestException("secret", "safe"),
        MockHttpServletRequest()).body?.detail,
    )
  }

  // --------------------------------------------------

  @Test
  fun unknownFailuresDoNotExposeExceptionText() {
    val logger = LoggerFactory.getLogger(KotlinbrellaErrorAdvice::class.java) as Logger
    val appender = ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>()
    appender.start()
    logger.addAppender(appender)

    val advice = KotlinbrellaErrorAdvice(KotlinbrellaWebProperties(), MockEnvironment())
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
    val result = KotlinbrellaErrorAdvice(KotlinbrellaWebProperties(), MockEnvironment())
      .invalidArgument(exception, MockHttpServletRequest())

    assertEquals(400, result.statusCode.value())
    val violations = result.body?.properties?.get(Constants.Web.VIOLATIONS_KEY) as List<*>

    assertEquals("value", (violations.first() as ErrorViolation).field)
  }

  // --------------------------------------------------

  @Test
  fun preservesResourceAndValidationStatuses() {
    val advice = KotlinbrellaErrorAdvice(KotlinbrellaWebProperties(), MockEnvironment())
    val request = MockHttpServletRequest()

    assertEquals(404, advice.notFound(request).statusCode.value())
    assertEquals(400, advice.constraintViolation(ConstraintViolationException(emptySet()),
      request).statusCode.value())
    assertEquals(400, advice.badRequest(request).statusCode.value())
    assertEquals(405, advice.methodNotAllowed(request).statusCode.value())
    assertEquals(415, advice.unsupportedMediaType(request).statusCode.value())
  }

  // --------------------------------------------------

  @Test
  fun rejectsUnsafeIncomingRequestId() {
    val request = MockHttpServletRequest()
    request.addHeader("X-Request-Id", "invalid\r\nheader")
    val response = MockHttpServletResponse()
    val filter = KotlinbrellaRequestIdFilter(KotlinbrellaWebProperties())

    filter.doFilter(request, response, MockFilterChain())

    assertTrue(response.getHeader("X-Request-Id")?.matches(Regex("[a-f0-9-]{36}")) == true)
    assertEquals(null, MDC.get(Constants.Web.TRACE_ID_KEY))
  }

  // --------------------------------------------------

  @Test
  fun propagatesValidRequestId() {
    val request = MockHttpServletRequest()
    request.addHeader("X-Request-Id", "safe-id")
    val response = MockHttpServletResponse()

    KotlinbrellaRequestIdFilter(KotlinbrellaWebProperties())
      .doFilter(request, response, MockFilterChain())

    assertEquals("safe-id", response.getHeader("X-Request-Id"))
    assertEquals("safe-id", request.getAttribute(Constants.Web.TRACE_ID_ATTRIBUTE))
  }

}

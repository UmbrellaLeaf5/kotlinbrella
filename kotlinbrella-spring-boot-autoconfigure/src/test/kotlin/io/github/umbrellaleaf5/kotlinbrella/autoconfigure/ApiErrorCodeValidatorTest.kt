package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class ApiErrorCodeValidatorTest {

  // MARK: Reject unknown error codes at startup
  // --------------------------------------------------

  @Test
  fun rejectsUnknownCode() {
    val validator = ApiErrorCodeValidator(mappingsFor(ErrorDocsBadCodeFixture(), "broken"))

    assertThrows(IllegalStateException::class.java) { validator.validate() }
  }

  // --------------------------------------------------

  @Test
  fun acceptsKnownCodes() {
    val validator = ApiErrorCodeValidator(mappingsFor(ErrorDocsFixtureController(), "single"))

    assertDoesNotThrow { validator.validate() }
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun mappingsFor(bean: Any, methodName: String): RequestMappingHandlerMapping {
    val method = bean.javaClass.getMethod(methodName)
    val handlerMethod = HandlerMethod(bean, method)

    return object : RequestMappingHandlerMapping() {
      override fun getHandlerMethods(): Map<RequestMappingInfo, HandlerMethod> =
        mapOf(RequestMappingInfo.paths("/probe").build() to handlerMethod)
    }
  }

}

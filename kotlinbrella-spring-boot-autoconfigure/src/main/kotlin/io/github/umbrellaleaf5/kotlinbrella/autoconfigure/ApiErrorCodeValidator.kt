package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class ApiErrorCodeValidator(
  private val mappings: RequestMappingHandlerMapping,
) {

  // MARK: Fail fast on unknown error codes
  // --------------------------------------------------

  /**
   * Checks every [ApiErrors] code at startup so a typo
   * cannot break spec generation at runtime.
   */
  @EventListener(ApplicationReadyEvent::class)
  fun validate() {
    for ((_, handler) in mappings.handlerMethods) {
      val errors = AnnotatedElementUtils.findMergedAnnotation(
        handler.method,
        ApiErrors::class.java,
      )?.value ?: continue

      for (error in errors) {
        try {
          ErrorCode.status(error.code)
        }

        catch (e: IllegalArgumentException) {
          throw IllegalStateException(
            "Unknown ApiError code '${error.code}' on ${handler.method}", e
          )
        }
      }
    }
  }

}

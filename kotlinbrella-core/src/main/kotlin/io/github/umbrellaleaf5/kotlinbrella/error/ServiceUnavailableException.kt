package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

class ServiceUnavailableException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.SERVICE_UNAVAILABLE,
  cause: Throwable? = null,
) : ApiException(Constants.Http.SERVICE_UNAVAILABLE, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.SERVICE_UNAVAILABLE,
    ): ServiceUnavailableException = ServiceUnavailableException(message, message, code)

  }

}

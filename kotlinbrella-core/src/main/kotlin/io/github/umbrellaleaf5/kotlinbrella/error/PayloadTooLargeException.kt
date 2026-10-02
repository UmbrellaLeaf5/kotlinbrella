package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class PayloadTooLargeException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.PAYLOAD_TOO_LARGE,
  cause: Throwable? = null,
) : ApiException(Constants.Http.PAYLOAD_TOO_LARGE, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.PAYLOAD_TOO_LARGE,
    ): PayloadTooLargeException = PayloadTooLargeException(message, message, code)

  }

}

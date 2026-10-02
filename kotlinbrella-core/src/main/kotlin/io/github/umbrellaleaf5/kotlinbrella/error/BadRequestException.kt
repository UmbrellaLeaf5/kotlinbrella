package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class BadRequestException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.BAD_REQUEST,
  cause: Throwable? = null,
) : ApiException(Constants.Http.BAD_REQUEST, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.BAD_REQUEST,
    ): BadRequestException = BadRequestException(message, message, code)

  }

}

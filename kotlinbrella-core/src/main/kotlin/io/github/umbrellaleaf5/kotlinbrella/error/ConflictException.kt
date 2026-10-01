package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

class ConflictException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.CONFLICT,
  cause: Throwable? = null,
) : ApiException(Constants.Http.CONFLICT, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.CONFLICT,
    ): ConflictException = ConflictException(message, message, code)

  }

}

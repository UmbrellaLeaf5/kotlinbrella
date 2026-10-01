package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

class ForbiddenException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.FORBIDDEN,
  cause: Throwable? = null,
) : ApiException(Constants.Http.FORBIDDEN, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.FORBIDDEN,
    ): ForbiddenException = ForbiddenException(message, message, code)

  }

}

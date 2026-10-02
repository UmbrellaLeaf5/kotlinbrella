package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class InternalServerException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.INTERNAL_ERROR,
  cause: Throwable? = null,
) : ApiException(Constants.Http.INTERNAL_ERROR, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.INTERNAL_ERROR,
    ): InternalServerException = InternalServerException(message, message, code)

  }

}

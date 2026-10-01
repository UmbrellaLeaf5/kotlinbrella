package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

class NotFoundException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.NOT_FOUND,
  cause: Throwable? = null,
) : ApiException(Constants.Http.NOT_FOUND, code, prodMessage, devMessage, cause) {

  companion object {

    // --------------------------------------------------

    @JvmStatic
    @JvmOverloads
    fun unified(
      message: String,
      code: String = ErrorCode.NOT_FOUND,
    ): NotFoundException = NotFoundException(message, message, code)

  }

}

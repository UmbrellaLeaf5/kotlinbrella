package io.github.umbrellaleaf5.kotlinbrella.error

class NotFoundException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.NOT_FOUND,
  cause: Throwable? = null,
) : ApiException(404, code, prodMessage, devMessage, cause) {

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

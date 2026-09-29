package io.github.umbrellaleaf5.kotlinbrella.error

class ConflictException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.CONFLICT,
  cause: Throwable? = null,
) : ApiException(409, code, prodMessage, devMessage, cause) {

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

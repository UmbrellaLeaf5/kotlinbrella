package io.github.umbrellaleaf5.kotlinbrella.error

class ForbiddenException @JvmOverloads constructor(
  devMessage: String,
  prodMessage: String,
  code: String = ErrorCode.FORBIDDEN,
  cause: Throwable? = null,
) : ApiException(403, code, prodMessage, devMessage, cause) {

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

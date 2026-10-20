package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class ForbiddenException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.FORBIDDEN,
  cause: Throwable? = null,
) : ApiException(Constants.Http.FORBIDDEN, code, message, cause)


package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class ConflictException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.CONFLICT,
  cause: Throwable? = null,
) : ApiException(Constants.Http.CONFLICT, code, message, cause)


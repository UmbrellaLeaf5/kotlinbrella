package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class BadRequestException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.BAD_REQUEST,
  cause: Throwable? = null,
) : ApiException(Constants.Http.BAD_REQUEST, code, message, cause)


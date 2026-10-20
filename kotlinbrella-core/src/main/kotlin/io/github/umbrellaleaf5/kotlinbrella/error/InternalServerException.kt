package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class InternalServerException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.INTERNAL_ERROR,
  cause: Throwable? = null,
) : ApiException(Constants.Http.INTERNAL_ERROR, code, message, cause)


package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class NotFoundException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.NOT_FOUND,
  cause: Throwable? = null,
) : ApiException(Constants.Http.NOT_FOUND, code, message, cause)


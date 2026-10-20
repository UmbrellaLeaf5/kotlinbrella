package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class ServiceUnavailableException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.SERVICE_UNAVAILABLE,
  cause: Throwable? = null,
) : ApiException(Constants.Http.SERVICE_UNAVAILABLE, code, message, cause)


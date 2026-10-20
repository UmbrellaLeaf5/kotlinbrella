package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class PayloadTooLargeException @JvmOverloads constructor(
  message: String,
  code: String = ErrorCode.PAYLOAD_TOO_LARGE,
  cause: Throwable? = null,
) : ApiException(Constants.Http.PAYLOAD_TOO_LARGE, code, message, cause)


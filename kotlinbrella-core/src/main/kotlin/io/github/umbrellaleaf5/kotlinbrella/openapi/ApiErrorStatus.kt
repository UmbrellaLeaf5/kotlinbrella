package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode

/**
 * Returns the HTTP status matching the error code.
 */
val ApiError.status: Int
  get() = ErrorCode.status(code)

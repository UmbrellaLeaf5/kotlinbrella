package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode

/**
 * Returns the error title for documentation and responses.
 */
val ApiError.title: String
  get() = ErrorCode.title(code)

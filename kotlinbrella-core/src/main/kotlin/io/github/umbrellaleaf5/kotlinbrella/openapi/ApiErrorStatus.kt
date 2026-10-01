package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode

/**
 * Возвращает HTTP-статус, соответствующий коду ошибки.
 */
val ApiError.status: Int
  get() = ErrorCode.status(code)

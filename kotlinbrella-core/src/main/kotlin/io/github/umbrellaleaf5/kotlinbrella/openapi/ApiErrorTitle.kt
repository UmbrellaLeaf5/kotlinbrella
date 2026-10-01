package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode

/**
 * Возвращает заголовок ошибки для документации и ответов.
 */
val ApiError.title: String
  get() = ErrorCode.title(code)

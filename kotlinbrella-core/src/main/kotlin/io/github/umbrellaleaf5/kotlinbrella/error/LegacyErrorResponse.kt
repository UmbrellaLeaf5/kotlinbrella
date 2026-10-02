package io.github.umbrellaleaf5.kotlinbrella.error

/**
 * Упрощённое тело ошибки для контрактов вида `{error, message}`.
 */
data class LegacyErrorResponse(
  val error: String,  // reason phrase статуса
  val message: String,  // безопасное публичное описание
)

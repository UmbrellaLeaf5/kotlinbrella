package io.github.umbrellaleaf5.kotlinbrella.error

/**
 * Simplified error body for `{error, message}` contracts.
 */
data class SimpleErrorResponse(
  val error: String,  // reason phrase статуса
  val message: String,  // безопасное публичное описание
)

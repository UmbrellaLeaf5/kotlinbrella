package io.github.umbrellaleaf5.kotlinbrella.error

/**
 * Simplified error body for `{error, message}` contracts.
 */
data class SimpleErrorResponse(
  val error: String,  // status reason phrase
  val message: String,  // client-facing description
)

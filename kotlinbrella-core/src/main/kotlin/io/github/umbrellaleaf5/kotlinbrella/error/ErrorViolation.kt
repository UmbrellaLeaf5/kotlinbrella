package io.github.umbrellaleaf5.kotlinbrella.error

data class ErrorViolation(
  val field: String?,  // null for object-level errors
  val message: String,
  val code: String,
)

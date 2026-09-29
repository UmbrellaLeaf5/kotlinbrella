package io.github.umbrellaleaf5.kotlinbrella.error

data class ErrorViolation(
  val field: String?,
  val message: String,
  val code: String,
)

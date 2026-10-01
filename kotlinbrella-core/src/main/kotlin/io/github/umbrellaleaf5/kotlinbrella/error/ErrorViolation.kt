package io.github.umbrellaleaf5.kotlinbrella.error

data class ErrorViolation(
  val field: String?,  // null для ошибок уровня объекта
  val message: String,
  val code: String,
)

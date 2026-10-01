package io.github.umbrellaleaf5.kotlinbrella.validation

data class EmailInput(
  @field:ValidEmail
  val email: String? = null,
)

package io.github.umbrellaleaf5.kotlinbrella.validation

data class RequiredFieldInput(
  @field:RequiredField
  val name: String? = null,
)

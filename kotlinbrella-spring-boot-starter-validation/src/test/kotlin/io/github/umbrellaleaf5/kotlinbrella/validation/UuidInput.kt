package io.github.umbrellaleaf5.kotlinbrella.validation

data class UuidInput(
  @field:ValidUUID
  val id: String? = null,
)

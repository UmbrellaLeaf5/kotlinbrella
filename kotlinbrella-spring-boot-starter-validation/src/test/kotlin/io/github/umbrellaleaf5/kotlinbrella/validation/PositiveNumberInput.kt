package io.github.umbrellaleaf5.kotlinbrella.validation

data class PositiveNumberInput(
  @field:ValidPositiveInt
  val count: String? = null,

  @field:ValidPositiveDouble
  val ratio: String? = null,
)

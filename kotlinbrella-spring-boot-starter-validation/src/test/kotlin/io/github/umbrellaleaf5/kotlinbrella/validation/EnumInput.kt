package io.github.umbrellaleaf5.kotlinbrella.validation

data class EnumInput(
  @field:ValidEnum(values = ["PRINT", "CUT"], name = "type")
  val type: String? = null,
)

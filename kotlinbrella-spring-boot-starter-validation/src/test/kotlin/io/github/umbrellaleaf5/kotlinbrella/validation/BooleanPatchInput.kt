package io.github.umbrellaleaf5.kotlinbrella.validation

@AtLeastOnePresent(properties = ["isActive"])
data class BooleanPatchInput(
  val isActive: Boolean? = null,
)

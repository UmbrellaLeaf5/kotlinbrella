package io.github.umbrellaleaf5.kotlinbrella.validation

@AtLeastOnePresent(properties = ["unknown"])
data class InvalidPatchInput(
  val name: String? = null,
)

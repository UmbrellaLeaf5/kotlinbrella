package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent

@AtLeastOnePresent(properties = ["value"])
data class WebFixturePatchInput(
  val value: String? = null,
)

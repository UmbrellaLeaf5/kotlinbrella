package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.umbrellaleaf5.kotlinbrella.samples.full.Constants
import io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent

@AtLeastOnePresent(properties = [Constants.Json.NAME])
data class ItemCreateRequest(
  @field:JsonProperty(Constants.Json.NAME)
  val name: String? = null,
)

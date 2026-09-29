package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent

@AtLeastOnePresent(properties = ["name"])
data class ItemCreateRequest(
  @field:JsonProperty("name")
  val name: String? = null,
)

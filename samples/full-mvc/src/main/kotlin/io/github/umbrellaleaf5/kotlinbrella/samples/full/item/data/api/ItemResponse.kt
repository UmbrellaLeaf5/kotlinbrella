package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.umbrellaleaf5.kotlinbrella.samples.full.Constants
import java.util.UUID

data class ItemResponse(
  @field:JsonProperty(Constants.Json.ID)
  val id: UUID,
  @field:JsonProperty(Constants.Json.OWNER_ID)
  val ownerId: UUID,
  @field:JsonProperty(Constants.Json.NAME)
  val name: String,
)

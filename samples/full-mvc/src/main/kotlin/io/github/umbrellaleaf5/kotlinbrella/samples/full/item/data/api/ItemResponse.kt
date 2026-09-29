package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ItemResponse(
  @field:JsonProperty("id")
  val id: UUID,
  @field:JsonProperty("owner_id")
  val ownerId: UUID,
  @field:JsonProperty("name")
  val name: String,
)

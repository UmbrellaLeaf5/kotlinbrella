package io.github.umbrellaleaf5.kotlinbrella.data.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * Response carrying the identifier of the created entity.
 *
 * The explicit creator keeps deserialization deterministic
 * on any Jackson setup without the Kotlin module.
 */
data class IdResponse @JsonCreator constructor(
  @param:JsonProperty("id")
  val id: UUID,  // идентификатор сущности
)

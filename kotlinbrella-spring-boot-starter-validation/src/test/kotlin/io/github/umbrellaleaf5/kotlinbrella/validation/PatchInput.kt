package io.github.umbrellaleaf5.kotlinbrella.validation

import java.util.UUID

@AtLeastOnePresent(properties = ["name", "count", "enabled", "tags", "metadata", "items"])
data class PatchInput(
  val name: String? = null,
  val count: Int? = null,
  val enabled: Boolean? = null,
  val tags: List<String>? = null,
  val metadata: Map<String, String>? = null,
  val items: Array<String>? = null,
  val technicalId: UUID = UUID.randomUUID(),
)

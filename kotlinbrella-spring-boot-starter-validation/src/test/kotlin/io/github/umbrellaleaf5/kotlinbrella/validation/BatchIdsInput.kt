package io.github.umbrellaleaf5.kotlinbrella.validation

data class BatchIdsInput(
  @field:ValidBatchIds
  val ids: List<String>? = null,
)

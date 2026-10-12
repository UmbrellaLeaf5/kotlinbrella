package io.github.umbrellaleaf5.kotlinbrella.validation

data class SmallBatchIdsInput(
  @field:ValidBatchIds(max = 2)
  val ids: List<String>? = null,
)

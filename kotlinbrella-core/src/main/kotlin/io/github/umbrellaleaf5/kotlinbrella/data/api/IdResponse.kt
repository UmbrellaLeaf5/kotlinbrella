package io.github.umbrellaleaf5.kotlinbrella.data.api

import java.util.UUID

/**
 * Ответ с идентификатором созданной сущности.
 */
data class IdResponse(
  val id: UUID,  // идентификатор сущности
)

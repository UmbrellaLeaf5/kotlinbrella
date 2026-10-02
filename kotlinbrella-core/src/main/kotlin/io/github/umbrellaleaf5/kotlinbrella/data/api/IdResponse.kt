package io.github.umbrellaleaf5.kotlinbrella.data.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * Ответ с идентификатором созданной сущности.
 *
 * Явный креатор делает десериализацию детерминированной
 * без Kotlin-модуля на любом Jackson.
 */
data class IdResponse @JsonCreator constructor(
  @param:JsonProperty("id")
  val id: UUID,  // идентификатор сущности
)

package io.github.umbrellaleaf5.kotlinbrella.samples.full.item

import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemCreateRequest
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemResponse
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.storage.ItemEntity
import io.github.umbrellaleaf5.kotlinbrella.util.checkFieldNotNullByName
import io.github.umbrellaleaf5.kotlinbrella.util.requireFieldNotNullByName
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ItemMapper {

  // MARK: Convert request to entity
  // --------------------------------------------------

  fun toEntity(request: ItemCreateRequest, ownerId: UUID): ItemEntity = ItemEntity(
    ownerId = ownerId,
    name = request.name.requireFieldNotNullByName { "name" },
  )

  // MARK: Convert persisted entity to response
  // --------------------------------------------------

  fun toResponse(item: ItemEntity): ItemResponse = ItemResponse(
    id = item.id.checkFieldNotNullByName { "id" },
    ownerId = item.ownerId,
    name = item.name,
  )

}

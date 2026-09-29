package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.storage

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ItemRepository : CrudRepository<ItemEntity, UUID> {

  // --------------------------------------------------

  fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean

}

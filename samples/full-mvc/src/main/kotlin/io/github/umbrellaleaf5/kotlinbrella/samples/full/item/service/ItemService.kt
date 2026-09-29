package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.service

import io.github.umbrellaleaf5.kotlinbrella.access.CheckOwnership
import io.github.umbrellaleaf5.kotlinbrella.data.findByIdOrThrow
import io.github.umbrellaleaf5.kotlinbrella.samples.full.Constants
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.ItemMapper
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemCreateRequest
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemResponse
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.storage.ItemRepository
import io.github.umbrellaleaf5.kotlinbrella.util.toUUIDOrThrow
import org.springframework.stereotype.Service

@Service
class ItemService(
  // repositories:
  private val itemRepository: ItemRepository,

  // mappers:
  private val itemMapper: ItemMapper,
) {

  // MARK: Create an item from validated input
  // --------------------------------------------------

  fun createItem(userIdString: String, request: ItemCreateRequest): ItemResponse {
    val userId = userIdString.toUUIDOrThrow()
    val item = itemMapper.toEntity(request, userId)
    val saved = itemRepository.save(item)

    return itemMapper.toResponse(saved)
  }

  // MARK: Read an owned item
  // --------------------------------------------------

  @CheckOwnership(resource = Constants.Resource.ITEM)
  fun getItem(userIdString: String, itemIdString: String): ItemResponse {
    val userId = userIdString.toUUIDOrThrow()
    val itemId = itemIdString.toUUIDOrThrow()
    val item = itemRepository.findByIdOrThrow(itemId, Constants.Entity.ITEM)

    return itemMapper.toResponse(item)
  }

}

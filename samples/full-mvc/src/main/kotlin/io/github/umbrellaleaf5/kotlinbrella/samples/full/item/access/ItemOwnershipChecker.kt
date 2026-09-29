package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.access

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker
import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.storage.ItemRepository
import io.github.umbrellaleaf5.kotlinbrella.util.toUUIDOrThrow
import org.springframework.stereotype.Component

@Component
class ItemOwnershipChecker(
  // repositories:
  private val itemRepository: ItemRepository,
) : AccessChecker {

  override val resource: String = "item"

  // MARK: Check item ownership with an indexed query
  // --------------------------------------------------

  override fun check(resourceIdString: String, userIdString: String): AccessDecision {
    val itemId = resourceIdString.toUUIDOrThrow()
    val userId = userIdString.toUUIDOrThrow()

    if (!itemRepository.existsById(itemId)) return AccessDecision.NOT_FOUND

    return if (itemRepository.existsByIdAndOwnerId(itemId, userId))
      AccessDecision.ALLOWED else AccessDecision.FORBIDDEN
  }

}

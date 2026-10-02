package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.api

import io.github.umbrellaleaf5.kotlinbrella.samples.full.Constants
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemCreateRequest
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemResponse
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.service.ItemService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@Tag(name = Constants.Api.TAG_ITEMS)
@RestController
class ItemController(
  // services:
  private val itemService: ItemService,
) : ItemApiSpec {

  // MARK: GET /api/item/{item_id}
  // --------------------------------------------------

  override fun getItem(userIdString: String, itemIdString: String): ItemResponse =
    itemService.getItem(userIdString, itemIdString)

  // MARK: POST /api/item
  // --------------------------------------------------

  override fun createItem(userIdString: String, request: ItemCreateRequest): ItemResponse =
    itemService.createItem(userIdString, request)

}

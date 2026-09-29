package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.api

import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiError
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemCreateRequest
import io.github.umbrellaleaf5.kotlinbrella.samples.full.item.data.api.ItemResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Items")
interface ItemApiSpec {

  // MARK: GET /api/item/{item_id}
  // --------------------------------------------------

  @Operation(summary = "Read an owned item")
  @ApiErrors([
    ApiError(400, "INVALID_UUID", "Invalid UUID"),
    ApiError(404, "NOT_FOUND", "Resource not found"),
  ])
  @GetMapping("/api/item/{item_id}")
  fun getItem(
    @RequestParam("user_id") userIdString: String,
    @PathVariable("item_id") itemIdString: String,
  ): ItemResponse

  // MARK: POST /api/item
  // --------------------------------------------------

  @Operation(summary = "Create an item")
  @ApiErrors([ApiError(400, "AT_LEAST_ONE_PRESENT", "Validation failed")])
  @PostMapping("/api/item")
  fun createItem(
    @RequestParam("user_id") userIdString: String,
    @Valid @RequestBody request: ItemCreateRequest,
  ): ItemResponse

}

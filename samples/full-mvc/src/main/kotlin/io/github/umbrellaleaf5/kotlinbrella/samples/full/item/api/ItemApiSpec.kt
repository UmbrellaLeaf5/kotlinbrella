package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.api

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiError
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors
import io.github.umbrellaleaf5.kotlinbrella.samples.full.Constants
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

@Tag(name = Constants.Api.TAG_ITEMS)
interface ItemApiSpec {

  // MARK: GET /api/item/{item_id}
  // --------------------------------------------------

  @Operation(summary = Constants.Api.GET_ITEM_SUMMARY)
  @ApiErrors([
    ApiError(ErrorCode.INVALID_UUID, Constants.Api.INVALID_UUID_DESCRIPTION),
    ApiError(ErrorCode.NOT_FOUND, Constants.Api.NOT_FOUND_DESCRIPTION),
  ])
  @GetMapping(Constants.Api.ITEM_BY_ID_PATH)
  fun getItem(
    @RequestParam(Constants.Api.USER_ID) userIdString: String,
    @PathVariable(Constants.Api.ITEM_ID) itemIdString: String,
  ): ItemResponse

  // MARK: POST /api/item
  // --------------------------------------------------

  @Operation(summary = Constants.Api.CREATE_ITEM_SUMMARY)
  @ApiErrors([ApiError(ErrorCode.AT_LEAST_ONE_PRESENT,
    Constants.Api.VALIDATION_FAILED_DESCRIPTION)])
  @PostMapping(Constants.Api.ITEM_PATH)
  fun createItem(
    @RequestParam(Constants.Api.USER_ID) userIdString: String,
    @Valid @RequestBody request: ItemCreateRequest,
  ): ItemResponse

}

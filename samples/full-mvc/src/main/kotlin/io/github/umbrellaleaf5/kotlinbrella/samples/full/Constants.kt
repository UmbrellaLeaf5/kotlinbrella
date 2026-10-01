package io.github.umbrellaleaf5.kotlinbrella.samples.full

import io.github.umbrellaleaf5.kotlinbrella.Constants as CoreConstants

object Constants {

  object Api {

    const val ITEM_PATH = "/api/item"
    const val ITEM_BY_ID_PATH = "/api/item/{item_id}"
    const val OPENAPI_PATH = "/v3/api-docs"
    const val ITEM_ID = "item_id"
    const val USER_ID = "user_id"
    const val TAG_ITEMS = "Items"
    const val GET_ITEM_SUMMARY = "Read an owned item"
    const val CREATE_ITEM_SUMMARY = "Create an item"
    const val INVALID_UUID_DESCRIPTION = CoreConstants.ErrorDescription.INVALID_UUID
    const val NOT_FOUND_DESCRIPTION = CoreConstants.ErrorDescription.RESOURCE_NOT_FOUND
    const val VALIDATION_FAILED_DESCRIPTION = CoreConstants.ErrorDescription.VALIDATION_FAILED

  }

  // --------------------------------------------------

  object Entity {

    const val ITEM = "Item"
    const val ITEM_TABLE = "sample_items"

  }

  // --------------------------------------------------

  object Resource {

    const val ITEM = "item"

  }

  // --------------------------------------------------

  object Json {

    const val ID = "id"
    const val OWNER_ID = "owner_id"
    const val NAME = "name"

  }

}

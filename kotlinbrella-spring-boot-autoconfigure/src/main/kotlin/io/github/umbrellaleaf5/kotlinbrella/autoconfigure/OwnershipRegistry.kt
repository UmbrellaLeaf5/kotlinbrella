package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker

class OwnershipRegistry(
  // services:
  checkers: List<AccessChecker>,
) {

  private val byResource: Map<String, AccessChecker>

  init {
    require(checkers.all { it.resource.isNotBlank() }) {
      Constants.Access.INVALID_RESOURCE
    }

    val duplicates = checkers.groupBy { it.resource }.filterValues { it.size > 1 }
    require(duplicates.isEmpty()) {
      "${Constants.Access.DUPLICATE_CHECKER}: ${duplicates.keys.joinToString()}"
    }

    byResource = checkers.associateBy { it.resource }
  }

  // --------------------------------------------------

  fun checker(resource: String): AccessChecker = byResource[resource]
    ?: throw IllegalStateException("${Constants.Access.MISSING_CHECKER}: $resource")

}

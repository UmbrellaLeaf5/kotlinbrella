package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker
import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision

class MutableAccessChecker : AccessChecker {

  override val resource: String = "listing"
  var decision: AccessDecision = AccessDecision.ALLOWED
  var lastResourceId: String? = null
  var lastUserId: String? = null

  // MARK: Return a controlled access decision
  // --------------------------------------------------

  override fun check(resourceIdString: String, userIdString: String): AccessDecision {
    lastResourceId = resourceIdString
    lastUserId = userIdString

    return decision
  }

}

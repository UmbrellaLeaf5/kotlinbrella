package io.github.umbrellaleaf5.kotlinbrella.access

interface AccessChecker {

  val resource: String

  // MARK: Decide access without loading an entity through the aspect
  // --------------------------------------------------

  fun check(resourceIdString: String, userIdString: String): AccessDecision

}

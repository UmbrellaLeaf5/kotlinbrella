package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.CheckOwnership
import io.github.umbrellaleaf5.kotlinbrella.access.DenialPolicy

open class AccessFixture {

  // MARK: Exercise convention-based arguments
  // --------------------------------------------------

  @CheckOwnership(resource = "listing")
  open fun access(userIdString: String, listingIdString: String): String = listingIdString

  // --------------------------------------------------

  @CheckOwnership(
    resource = "listing",
    resourceIdParam = "customIdString",
    userIdParam = "ownerIdString",
    policy = DenialPolicy.FORBIDDEN,
  )
  open fun explicit(ownerIdString: String, customIdString: String): String = customIdString

  // --------------------------------------------------

  @CheckOwnership(resource = "unknown")
  open fun missing(userIdString: String, unknownIdString: String): String = unknownIdString

  // --------------------------------------------------

  @CheckOwnership(resource = "listing", resourceIdParam = "wrongName")
  open fun broken(userIdString: String, listingIdString: String): String = listingIdString

  // --------------------------------------------------

  open fun selfInvocation(userIdString: String, listingIdString: String): String =
    access(userIdString, listingIdString)

  // --------------------------------------------------

  @CheckOwnership(resource = "listing")
  open fun templated(actorIdString: String, listingKeyString: String): String = listingKeyString

}

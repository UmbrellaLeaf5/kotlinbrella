package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.CheckOwnership

interface AccessFixtureSpec {

  // --------------------------------------------------

  @CheckOwnership(resource = "listing")
  fun access(userIdString: String, listingIdString: String): String

}

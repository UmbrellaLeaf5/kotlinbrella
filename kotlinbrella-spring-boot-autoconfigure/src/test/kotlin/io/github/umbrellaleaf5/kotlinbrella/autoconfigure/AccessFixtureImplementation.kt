package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

open class AccessFixtureImplementation : AccessFixtureSpec {

  // --------------------------------------------------

  override fun access(userIdString: String, listingIdString: String): String = listingIdString

}

package io.github.umbrellaleaf5.kotlinbrella.openapi

interface OpenApiFixtureSpec {

  // --------------------------------------------------

  @ApiErrors([ApiError("FORBIDDEN", "Access denied")])
  fun documented(): String

}

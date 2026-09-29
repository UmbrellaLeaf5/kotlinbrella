package io.github.umbrellaleaf5.kotlinbrella.openapi

interface OpenApiFixtureSpec {

  // --------------------------------------------------

  @ApiErrors([ApiError(403, "FORBIDDEN", "Access denied")])
  fun documented(): String

}

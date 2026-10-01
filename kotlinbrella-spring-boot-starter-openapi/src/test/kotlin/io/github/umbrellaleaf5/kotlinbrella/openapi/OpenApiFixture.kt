package io.github.umbrellaleaf5.kotlinbrella.openapi

class OpenApiFixture {

  // MARK: Document multiple failures per HTTP status
  // --------------------------------------------------

  @ApiErrors([
    ApiError("BAD_REQUEST", "Invalid input"),
    ApiError("INVALID_UUID", "Invalid identifier"),
    ApiError("NOT_FOUND", "Not found"),
  ])
  fun documented(): String = ""

}

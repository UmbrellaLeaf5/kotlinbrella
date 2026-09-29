package io.github.umbrellaleaf5.kotlinbrella.openapi

class OpenApiFixture {

  // MARK: Document multiple failures per HTTP status
  // --------------------------------------------------

  @ApiErrors([
    ApiError(400, "BAD_REQUEST", "Invalid input"),
    ApiError(400, "INVALID_UUID", "Invalid identifier"),
    ApiError(404, "NOT_FOUND", "Not found"),
  ])
  fun documented(): String = ""

}

package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiError
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors

class ErrorDocsBadCodeFixture {

  // MARK: Exercise unknown error codes
  // --------------------------------------------------

  @ApiErrors(
    [
      ApiError(
        code = "NO_SUCH_CODE",
        detail = "Broken",
      ),
    ],
  )
  fun broken(): String = "ok"

}

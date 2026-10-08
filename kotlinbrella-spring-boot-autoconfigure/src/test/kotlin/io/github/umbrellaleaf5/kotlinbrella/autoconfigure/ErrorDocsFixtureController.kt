package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiError
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors

class ErrorDocsFixtureController {

  // MARK: Exercise error documentation rendering
  // --------------------------------------------------

  @ApiErrors(
    [
      ApiError(
        code = ErrorCode.NOT_FOUND,
        detail = "Calculation not found",
      ),
    ],
  )
  fun single(): String = "ok"

  // --------------------------------------------------

  @ApiErrors(
    [
      ApiError(
        code = ErrorCode.BAD_REQUEST,
        detail = "First problem",
      ),
      ApiError(
        code = ErrorCode.INVALID_UUID,
        detail = "Second problem",
      ),
    ],
  )
  fun multiple(): String = "ok"

  // --------------------------------------------------

  fun plain(): String = "ok"

}

package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.error.ConflictException

/**
 * Fails a two-database delete when the second side
 * removed fewer rows than expected, so the caller
 * retries the idempotent batch instead of leaving orphans.
 */
fun verifyBiDelete(expected: Int, actual: Int, target: String) {
  if (expected == actual) return

  throw ConflictException.unified(
    "Partial delete of $target: expected $expected, removed $actual, please retry"
  )
}

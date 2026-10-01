package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.Constants

// MARK: Named input checks
// --------------------------------------------------

inline fun <T : Any> T?.requireNotNullByName(name: () -> String): T =
  this ?: throw IllegalArgumentException("${name()}${Constants.Validation.NULL_SUFFIX}")

// --------------------------------------------------

inline fun <T : Any> T?.requireFieldNotNullByName(name: () -> String): T =
  this ?: throw IllegalArgumentException(
    "${Constants.Validation.FIELD_PREFIX}${name()}${Constants.Validation.NULL_SUFFIX}",
  )

// MARK: Named state checks
// --------------------------------------------------

inline fun <T : Any> T?.checkNotNullByName(name: () -> String): T =
  this ?: throw IllegalStateException("${name()}${Constants.Validation.NULL_SUFFIX}")

// --------------------------------------------------

inline fun <T : Any> T?.checkFieldNotNullByName(name: () -> String): T =
  this ?: throw IllegalStateException(
    "${Constants.Validation.FIELD_PREFIX}${name()}${Constants.Validation.NULL_SUFFIX}",
  )

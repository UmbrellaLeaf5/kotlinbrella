package io.github.umbrellaleaf5.kotlinbrella.util

// MARK: Named input checks
// --------------------------------------------------

inline fun <T : Any> T?.requireNotNullByName(name: () -> String): T =
  this ?: throw IllegalArgumentException("${name()} must not be null")

// --------------------------------------------------

inline fun <T : Any> T?.requireFieldNotNullByName(name: () -> String): T =
  this ?: throw IllegalArgumentException("Field ${name()} must not be null")

// MARK: Named state checks
// --------------------------------------------------

inline fun <T : Any> T?.checkNotNullByName(name: () -> String): T =
  this ?: throw IllegalStateException("${name()} must not be null")

// --------------------------------------------------

inline fun <T : Any> T?.checkFieldNotNullByName(name: () -> String): T =
  this ?: throw IllegalStateException("Field ${name()} must not be null")

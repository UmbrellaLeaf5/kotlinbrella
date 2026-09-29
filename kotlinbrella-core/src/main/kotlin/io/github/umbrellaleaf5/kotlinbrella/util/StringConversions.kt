package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.UUID

// MARK: Parse client values
// --------------------------------------------------

fun String.toUUIDOrThrow(): UUID = try {
  UUID.fromString(this)
}

catch (exception: IllegalArgumentException) {
  throw BadRequestException("Invalid UUID: $this", "Invalid UUID", ErrorCode.INVALID_UUID, exception)
}

// --------------------------------------------------

fun String.toIntOrThrow(): Int = toIntOrNull()
  ?: throw BadRequestException.unified("Invalid integer", ErrorCode.INVALID_INTEGER)

// --------------------------------------------------

fun String.toLongOrThrow(): Long = toLongOrNull()
  ?: throw BadRequestException.unified("Invalid long", ErrorCode.INVALID_LONG)

// --------------------------------------------------

fun String.toDoubleOrThrow(): Double = toDoubleOrNull()?.takeIf { it.isFinite() }
  ?: throw BadRequestException.unified("Invalid double", ErrorCode.INVALID_DOUBLE)

// --------------------------------------------------

fun String.toInstantOrThrow(): Instant = try {
  Instant.parse(this)
}

catch (exception: DateTimeParseException) {
  throw BadRequestException(
    "Invalid instant: $this",
    "Invalid instant",
    ErrorCode.INVALID_INSTANT,
    exception,
  )
}

// --------------------------------------------------

inline fun <reified T : Enum<T>> String.toEnumOrThrow(): T =
  enumValues<T>().firstOrNull { it.name == this }
    ?: throw BadRequestException.unified("Invalid enum value", ErrorCode.INVALID_ENUM)

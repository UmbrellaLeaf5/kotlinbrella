package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.Constants
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
  throw BadRequestException(
    "${Constants.ErrorDescription.INVALID_UUID_PREFIX}$this",
    Constants.ErrorDescription.INVALID_UUID,
    ErrorCode.INVALID_UUID,
    exception,
  )
}

// --------------------------------------------------

fun String.toIntOrThrow(): Int = toIntOrNull()
  ?: throw BadRequestException.unified(
    Constants.ErrorDescription.INVALID_INTEGER,
    ErrorCode.INVALID_INTEGER,
  )

// --------------------------------------------------

fun String.toLongOrThrow(): Long = toLongOrNull()
  ?: throw BadRequestException.unified(
    Constants.ErrorDescription.INVALID_LONG,
    ErrorCode.INVALID_LONG,
  )

// --------------------------------------------------

fun String.toDoubleOrThrow(): Double = toDoubleOrNull()?.takeIf { it.isFinite() }
  ?: throw BadRequestException.unified(
    Constants.ErrorDescription.INVALID_DOUBLE,
    ErrorCode.INVALID_DOUBLE,
  )

// --------------------------------------------------

fun String.toInstantOrThrow(): Instant = try {
  Instant.parse(this)
}

catch (exception: DateTimeParseException) {
  throw BadRequestException(
    "${Constants.ErrorDescription.INVALID_INSTANT_PREFIX}$this",
    Constants.ErrorDescription.INVALID_INSTANT,
    ErrorCode.INVALID_INSTANT,
    exception,
  )
}

// --------------------------------------------------

inline fun <reified T : Enum<T>> String.toEnumOrThrow(): T =
  enumValues<T>().firstOrNull { it.name == this }
    ?: throw BadRequestException.unified(
      Constants.ErrorDescription.INVALID_ENUM,
      ErrorCode.INVALID_ENUM,
    )

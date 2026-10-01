package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.Constants
import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import java.time.Instant
import java.util.UUID

object Conversions {

  // MARK: Java conversion facade
  // --------------------------------------------------

  @JvmStatic
  fun uuid(value: String): UUID = value.toUUIDOrThrow()

  // --------------------------------------------------

  @JvmStatic
  fun integer(value: String): Int = value.toIntOrThrow()

  // --------------------------------------------------

  @JvmStatic
  fun long(value: String): Long = value.toLongOrThrow()

  // --------------------------------------------------

  @JvmStatic
  fun double(value: String): Double = value.toDoubleOrThrow()

  // --------------------------------------------------

  @JvmStatic
  fun instant(value: String): Instant = value.toInstantOrThrow()

  // --------------------------------------------------

  @JvmStatic
  fun <T : Enum<T>> enumValue(value: String, type: Class<T>): T =
    type.enumConstants.firstOrNull { it.name == value }
      ?: throw BadRequestException.unified(
        Constants.ErrorDescription.INVALID_ENUM,
        ErrorCode.INVALID_ENUM,
      )

}

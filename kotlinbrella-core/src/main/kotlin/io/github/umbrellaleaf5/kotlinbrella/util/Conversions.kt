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

  // --------------------------------------------------

  @JvmStatic
  fun uuidOrNull(value: String?): UUID? = value.toUUIDOrNull()

  // --------------------------------------------------

  @JvmStatic
  fun integerOrNull(value: String?): Int? = value.toIntOrNull()

  // --------------------------------------------------

  @JvmStatic
  fun longOrNull(value: String?): Long? = value.toLongOrNull()

  // --------------------------------------------------

  @JvmStatic
  fun doubleOrNull(value: String?): Double? = value.toDoubleOrNull()

  // --------------------------------------------------

  @JvmStatic
  fun instantOrNull(value: String?): Instant? = value.toInstantOrNull()

  // --------------------------------------------------

  @JvmStatic
  fun <T : Enum<T>> enumValueOrNull(value: String?, type: Class<T>): T? =
    value?.let { text -> type.enumConstants.firstOrNull { it.name == text } }

}

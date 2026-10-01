package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

object ErrorCode {
  const val BAD_REQUEST = Constants.ErrorCodes.BAD_REQUEST
  const val NOT_FOUND = Constants.ErrorCodes.NOT_FOUND
  const val CONFLICT = Constants.ErrorCodes.CONFLICT
  const val FORBIDDEN = Constants.ErrorCodes.FORBIDDEN
  const val METHOD_NOT_ALLOWED = Constants.ErrorCodes.METHOD_NOT_ALLOWED
  const val UNSUPPORTED_MEDIA_TYPE = Constants.ErrorCodes.UNSUPPORTED_MEDIA_TYPE
  const val INTERNAL_ERROR = Constants.ErrorCodes.INTERNAL_ERROR
  const val INVALID_UUID = Constants.ErrorCodes.INVALID_UUID
  const val INVALID_INTEGER = Constants.ErrorCodes.INVALID_INTEGER
  const val INVALID_LONG = Constants.ErrorCodes.INVALID_LONG
  const val INVALID_DOUBLE = Constants.ErrorCodes.INVALID_DOUBLE
  const val INVALID_INSTANT = Constants.ErrorCodes.INVALID_INSTANT
  const val INVALID_ENUM = Constants.ErrorCodes.INVALID_ENUM
  const val AT_LEAST_ONE_PRESENT = Constants.ErrorCodes.AT_LEAST_ONE_PRESENT
  const val PAYLOAD_TOO_LARGE = Constants.ErrorCodes.PAYLOAD_TOO_LARGE
  const val SERVICE_UNAVAILABLE = Constants.ErrorCodes.SERVICE_UNAVAILABLE

  // MARK: Resolve the HTTP status of a documented error code
  // --------------------------------------------------

  @JvmStatic
  fun status(code: String): Int = when (code) {
    BAD_REQUEST, INVALID_UUID, INVALID_INTEGER, INVALID_LONG, INVALID_DOUBLE,
    INVALID_INSTANT, INVALID_ENUM, AT_LEAST_ONE_PRESENT -> Constants.Http.BAD_REQUEST
    FORBIDDEN -> Constants.Http.FORBIDDEN
    NOT_FOUND -> Constants.Http.NOT_FOUND
    METHOD_NOT_ALLOWED -> Constants.Http.METHOD_NOT_ALLOWED
    CONFLICT -> Constants.Http.CONFLICT
    PAYLOAD_TOO_LARGE -> Constants.Http.PAYLOAD_TOO_LARGE
    UNSUPPORTED_MEDIA_TYPE -> Constants.Http.UNSUPPORTED_MEDIA_TYPE
    INTERNAL_ERROR -> Constants.Http.INTERNAL_ERROR
    SERVICE_UNAVAILABLE -> Constants.Http.SERVICE_UNAVAILABLE
    else -> throw IllegalArgumentException("Unknown error code: $code")
  }

  // MARK: Resolve the reason phrase of a documented error code
  // --------------------------------------------------

  @JvmStatic
  fun title(code: String): String = when (code) {
    BAD_REQUEST -> Constants.Http.Title.BAD_REQUEST
    FORBIDDEN -> Constants.Http.Title.FORBIDDEN
    NOT_FOUND -> Constants.Http.Title.NOT_FOUND
    METHOD_NOT_ALLOWED -> Constants.Http.Title.METHOD_NOT_ALLOWED
    CONFLICT -> Constants.Http.Title.CONFLICT
    PAYLOAD_TOO_LARGE -> Constants.Http.Title.PAYLOAD_TOO_LARGE
    UNSUPPORTED_MEDIA_TYPE -> Constants.Http.Title.UNSUPPORTED_MEDIA_TYPE
    INTERNAL_ERROR -> Constants.Http.Title.INTERNAL_ERROR
    SERVICE_UNAVAILABLE -> Constants.Http.Title.SERVICE_UNAVAILABLE
    INVALID_UUID, INVALID_INTEGER, INVALID_LONG, INVALID_DOUBLE,
    INVALID_INSTANT, INVALID_ENUM, AT_LEAST_ONE_PRESENT -> Constants.Http.Title.BAD_REQUEST
    else -> throw IllegalArgumentException("Unknown error code: $code")
  }
}

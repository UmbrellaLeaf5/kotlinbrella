package io.github.umbrellaleaf5.kotlinbrella

object Constants {

  object ErrorCodes {

    const val BAD_REQUEST = "BAD_REQUEST"
    const val NOT_FOUND = "NOT_FOUND"
    const val CONFLICT = "CONFLICT"
    const val FORBIDDEN = "FORBIDDEN"
    const val METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED"
    const val UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val INVALID_UUID = "INVALID_UUID"
    const val INVALID_INTEGER = "INVALID_INTEGER"
    const val INVALID_LONG = "INVALID_LONG"
    const val INVALID_DOUBLE = "INVALID_DOUBLE"
    const val INVALID_INSTANT = "INVALID_INSTANT"
    const val INVALID_ENUM = "INVALID_ENUM"
    const val AT_LEAST_ONE_PRESENT = "AT_LEAST_ONE_PRESENT"
    const val PAYLOAD_TOO_LARGE = "PAYLOAD_TOO_LARGE"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"

  }

  // --------------------------------------------------

  object Http {

    const val BAD_REQUEST = 400
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val METHOD_NOT_ALLOWED = 405
    const val CONFLICT = 409
    const val PAYLOAD_TOO_LARGE = 413
    const val UNSUPPORTED_MEDIA_TYPE = 415
    const val INTERNAL_ERROR = 500
    const val SERVICE_UNAVAILABLE = 503
    const val MAX_ERROR_STATUS = 599

    object Title {
      const val BAD_REQUEST = "Bad Request"
      const val FORBIDDEN = "Forbidden"
      const val NOT_FOUND = "Not Found"
      const val METHOD_NOT_ALLOWED = "Method Not Allowed"
      const val CONFLICT = "Conflict"
      const val PAYLOAD_TOO_LARGE = "Payload Too Large"
      const val UNSUPPORTED_MEDIA_TYPE = "Unsupported Media Type"
      const val INTERNAL_ERROR = "Internal Server Error"
      const val SERVICE_UNAVAILABLE = "Service Unavailable"
    }

  }

  // --------------------------------------------------

  object ErrorDescription {

    const val INVALID_UUID = "Invalid UUID"
    const val INVALID_UUID_PREFIX = "Invalid UUID: "
    const val INVALID_INTEGER = "Invalid integer"
    const val INVALID_LONG = "Invalid long"
    const val INVALID_DOUBLE = "Invalid double"
    const val INVALID_INSTANT = "Invalid instant"
    const val INVALID_INSTANT_PREFIX = "Invalid instant: "
    const val INVALID_ENUM = "Invalid enum value"
    const val RESOURCE_NOT_FOUND = "Resource not found"
    const val VALIDATION_FAILED = "Validation failed"
    const val ACCESS_DENIED = "Access denied"

  }

  // --------------------------------------------------

  object Validation {

    const val FIELD_PREFIX = "Field "
    const val NULL_SUFFIX = " must not be null"

  }

  // --------------------------------------------------

  object Pattern {

    const val ERROR_CODE = "[A-Z][A-Z0-9_]*"

  }

}

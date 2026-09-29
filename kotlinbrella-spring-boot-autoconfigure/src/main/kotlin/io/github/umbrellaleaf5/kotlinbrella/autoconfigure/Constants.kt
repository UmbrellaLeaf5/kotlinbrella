package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

object Constants {

  object Web {

    const val REQUEST_ID_HEADER = "X-Request-Id"
    const val REQUEST_ID_PATTERN = "[A-Za-z0-9_-]{1,64}"
    const val TRACE_ID_ATTRIBUTE = "kotlinbrella.traceId"
    const val TRACE_ID_KEY = "traceId"
    const val CODE_KEY = "code"
    const val VIOLATIONS_KEY = "violations"

  }

  // --------------------------------------------------

  object ErrorDescription {

    const val INVALID_VALUE = "Invalid value"
    const val VALIDATION_FAILED = "Validation failed"
    const val INVALID_REQUEST = "Invalid request"
    const val RESOURCE_NOT_FOUND = "Resource not found"
    const val METHOD_NOT_ALLOWED = "Method not allowed"
    const val UNSUPPORTED_MEDIA_TYPE = "Unsupported media type"
    const val INTERNAL_ERROR = "Internal server error"

  }

  // --------------------------------------------------

  object Validation {

    const val AT_LEAST_ONE_PRESENT_NAME = "AtLeastOnePresent"
    const val AT_LEAST_ONE_PRESENT_CLASS =
      "io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent"

  }

}

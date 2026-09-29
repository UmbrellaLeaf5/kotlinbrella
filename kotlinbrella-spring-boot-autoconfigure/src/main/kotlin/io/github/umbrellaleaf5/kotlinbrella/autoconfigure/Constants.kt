package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

object Constants {

  object Access {

    const val RESOURCE_ID_TEMPLATE = "{resource}IdString"
    const val RESOURCE_PLACEHOLDER = "{resource}"
    const val USER_ID_PARAMETER = "userIdString"
    const val INVALID_RESOURCE = "Resource key must not be blank"
    const val DUPLICATE_CHECKER = "Duplicate access checker for resource"
    const val MISSING_CHECKER = "No access checker registered for resource"
    const val MISSING_PARAMETER = "Ownership parameter not found"
    const val RESOURCE_NOT_FOUND = "Resource not found"
    const val ACCESS_DENIED = "Access denied"

  }

  // --------------------------------------------------

  object ApiSpec {

    const val PROBLEM_MEDIA_TYPE = "application/problem+json"
    const val PROBLEM_SCHEMA = "KotlinbrellaProblem"
    const val VIOLATION_SCHEMA = "KotlinbrellaViolation"
    const val PROBLEM_REFERENCE = "#/components/schemas/KotlinbrellaProblem"
    const val VIOLATION_REFERENCE = "#/components/schemas/KotlinbrellaViolation"
    const val TITLE_KEY = "title"
    const val STATUS_KEY = "status"
    const val DETAIL_KEY = "detail"
    const val INSTANCE_KEY = "instance"
    const val FIELD_KEY = "field"
    const val MESSAGE_KEY = "message"
    const val SAMPLE_INSTANCE = "/example"
    const val SAMPLE_TRACE_ID = "example-trace-id"
    const val STATUS_DESCRIPTION = "HTTP problem status"

  }

  // --------------------------------------------------

  object Web {

    const val TYPE_KEY = "type"
    const val ABOUT_BLANK = "about:blank"
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
    const val CONFLICT = "Resource conflict"

  }

  // --------------------------------------------------

  object Validation {

    const val AT_LEAST_ONE_PRESENT_NAME = "AtLeastOnePresent"
    const val AT_LEAST_ONE_PRESENT_CLASS =
      "io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent"

  }

}

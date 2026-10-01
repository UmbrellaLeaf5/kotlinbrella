package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.Constants as CoreConstants

object Constants {

  object Access {

    const val RESOURCE_ID_TEMPLATE = "{resource}IdString"
    const val RESOURCE_PLACEHOLDER = "{resource}"
    const val USER_ID_PARAMETER = "userIdString"
    const val INVALID_RESOURCE = "Resource key must not be blank"
    const val DUPLICATE_CHECKER = "Duplicate access checker for resource"
    const val MISSING_CHECKER = "No access checker registered for resource"
    const val MISSING_PARAMETER = "Ownership parameter not found"
    const val ACCESS_DENIED = CoreConstants.ErrorDescription.ACCESS_DENIED
    const val OWNERSHIP_POINTCUT = "@annotation(checkOwnership)"
    const val DECISION_LOG = "Ownership check for {} resulted in {}"

  }

  // --------------------------------------------------

  object ApiSpec {

    const val PROBLEM_MEDIA_TYPE = "application/problem+json"
    const val PROBLEM_SCHEMA = "KotlinbrellaProblem"
    const val VIOLATION_SCHEMA = "KotlinbrellaViolation"
    const val SCHEMA_PREFIX = "#/components/schemas/"
    const val PROBLEM_REFERENCE = SCHEMA_PREFIX + PROBLEM_SCHEMA
    const val VIOLATION_REFERENCE = SCHEMA_PREFIX + VIOLATION_SCHEMA
    const val TITLE_KEY = "title"
    const val STATUS_KEY = "status"
    const val DETAIL_KEY = "detail"
    const val INSTANCE_KEY = "instance"
    const val FIELD_KEY = "field"
    const val MESSAGE_KEY = "message"
    const val SAMPLE_INSTANCE = "/example"
    const val SAMPLE_TRACE_ID = "example-trace-id"
    const val STATUS_DESCRIPTION = "HTTP problem status"
    const val UNSUPPORTED_STATUS = "Unsupported API error status"

  }

  // --------------------------------------------------

  object Web {

    const val TYPE_KEY = "type"
    const val ABOUT_BLANK = "about:blank"
    const val REQUEST_ID_HEADER = "X-Request-Id"
    const val TRACE_ID_ATTRIBUTE = "kotlinbrella.traceId"
    const val TRACE_ID_KEY = "traceId"
    const val CODE_KEY = "code"
    const val VIOLATIONS_KEY = "violations"
    const val UNKNOWN_FAILURE_LOG = "Unexpected request failure"
    const val EXPECTED_FAILURE_LOG = "Expected client failure: {}"

  }

  // --------------------------------------------------

  object ErrorDescription {

    const val INVALID_VALUE = "Invalid value"
    const val VALIDATION_FAILED = CoreConstants.ErrorDescription.VALIDATION_FAILED
    const val INVALID_REQUEST = "Invalid request"
    const val RESOURCE_NOT_FOUND = CoreConstants.ErrorDescription.RESOURCE_NOT_FOUND
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

  // --------------------------------------------------

  object Configuration {

    const val WEB_PREFIX = "kotlinbrella.web"
    const val ACCESS_PREFIX = "kotlinbrella.access"
    const val OPENAPI_PREFIX = "kotlinbrella.openapi"
    const val DATA_JPA_ERRORS_PREFIX = "kotlinbrella.data-jpa.errors"
    const val ENABLED = "enabled"

  }

  // --------------------------------------------------

  object Pattern {

    const val REQUEST_ID = "[A-Za-z0-9_-]{1,64}"

  }

}

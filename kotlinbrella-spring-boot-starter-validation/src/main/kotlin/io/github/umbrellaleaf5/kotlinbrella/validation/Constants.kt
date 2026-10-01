package io.github.umbrellaleaf5.kotlinbrella.validation

object Constants {

  object Validation {

    const val MESSAGE_TEMPLATE = "{kotlinbrella.validation.AT_LEAST_ONE_PRESENT}"
    const val EMPTY_PROPERTY_LIST = "At least one property name is required"
    const val BLANK_PROPERTY_NAME = "Property names must not be blank"
    const val DUPLICATE_PROPERTY_NAME = "Property names must be unique"
    const val UNKNOWN_PROPERTY = "Unknown property in AtLeastOnePresent"
    const val BOOLEAN_GETTER_PREFIX = "is"
    const val INVALID_UUID = "Invalid UUID format"
    const val INVALID_EMAIL = "Invalid email format"
    const val INVALID_ENUM = "Invalid enum value"
    const val ENUM_FIELD = "field"
    const val REQUIRED_FIELD = "This field is required"
    const val INVALID_POSITIVE_NUMBER = "Must be a positive number"
    const val INVALID_NUMBER_FORMAT = "Invalid number format"
    const val INVALID_INTEGER_FORMAT = "Invalid integer format"
    const val MUST_BE_POSITIVE = "Must be a positive number"
    const val MUST_BE_NON_NEGATIVE = "Must be a non-negative number"
    const val MUST_BE_POSITIVE_INT = "Must be a positive integer"
    const val MUST_BE_NON_NEGATIVE_INT = "Must be a non-negative integer"
    const val INVALID_ENUM_PREFIX = "Invalid "
    const val ALLOWED_VALUES = ". Allowed values: "
    const val REJECTED_VALUE = " (rejected value: "
    const val REJECTED_VALUE_END = ")"

  }

  object Pattern {
    const val UUID =
      "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    const val EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
  }

}

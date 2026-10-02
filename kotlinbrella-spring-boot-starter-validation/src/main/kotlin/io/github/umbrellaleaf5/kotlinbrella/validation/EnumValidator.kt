package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<ValidEnum, String> {

  private lateinit var allowedValues: List<String>
  private lateinit var fieldName: String

  // MARK: Initialize allowed values
  // --------------------------------------------------

  override fun initialize(constraintAnnotation: ValidEnum) {
    allowedValues = constraintAnnotation.values.toList()
    fieldName = constraintAnnotation.name
  }

  // MARK: Validate a named value
  // --------------------------------------------------

  override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
    if (value == null) return false

    if (value.trim() in allowedValues) return true

    context.disableDefaultConstraintViolation()
    context.buildConstraintViolationWithTemplate(
      Constants.Validation.INVALID_ENUM_PREFIX + fieldName + Constants.Validation.ALLOWED_VALUES +
        allowedValues.joinToString(", "),
    ).addConstraintViolation()

    return false
  }

}

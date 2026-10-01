package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PositiveDoubleValidator : ConstraintValidator<ValidPositiveDouble, String> {

  private var allowZero = false

  // MARK: Initialize positivity rule
  // --------------------------------------------------

  override fun initialize(constraintAnnotation: ValidPositiveDouble) {
    allowZero = constraintAnnotation.allowZero
  }

  // MARK: Validate textual double
  // --------------------------------------------------

  override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
    if (value == null) return true

    val trimmed = value.trim()
    val parsed = trimmed.toDoubleOrNull()

    if (parsed == null || trimmed.isEmpty()) {
      violation(context, "${Constants.Validation.INVALID_NUMBER_FORMAT}: '$trimmed'")

      return false
    }

    if (parsed > 0 || (allowZero && parsed == 0.0)) return true

    val reason = if (allowZero) Constants.Validation.MUST_BE_NON_NEGATIVE
      else Constants.Validation.MUST_BE_POSITIVE
    violation(context, reason + Constants.Validation.REJECTED_VALUE + trimmed +
      Constants.Validation.REJECTED_VALUE_END)

    return false
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun violation(context: ConstraintValidatorContext, message: String) {
    context.disableDefaultConstraintViolation()
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation()
  }

}

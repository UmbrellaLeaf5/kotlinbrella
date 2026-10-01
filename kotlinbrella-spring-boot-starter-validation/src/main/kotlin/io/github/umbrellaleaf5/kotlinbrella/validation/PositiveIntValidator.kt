package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PositiveIntValidator : ConstraintValidator<ValidPositiveInt, String> {

  private var allowZero = false

  // MARK: Initialize positivity rule
  // --------------------------------------------------

  override fun initialize(constraintAnnotation: ValidPositiveInt) {
    allowZero = constraintAnnotation.allowZero
  }

  // MARK: Validate textual integer
  // --------------------------------------------------

  override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
    if (value == null) return true

    val trimmed = value.trim()
    val parsed = trimmed.toLongOrNull()

    if (parsed == null) {
      val reason = if ('.' in trimmed) Constants.Validation.INVALID_INTEGER_FORMAT
        else Constants.Validation.INVALID_NUMBER_FORMAT
      violation(context, "$reason: '$trimmed'")

      return false
    }

    if (parsed > 0 || (allowZero && parsed == 0L)) return true

    val reason = if (allowZero) Constants.Validation.MUST_BE_NON_NEGATIVE_INT
      else Constants.Validation.MUST_BE_POSITIVE_INT
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

package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidBatchIdsValidator : ConstraintValidator<ValidBatchIds, Collection<*>> {

  private var max: Int = 100

  // MARK: Read annotation configuration
  // --------------------------------------------------

  override fun initialize(constraintAnnotation: ValidBatchIds) {
    max = constraintAnnotation.max
    require(max > 0) { "ValidBatchIds max must be positive" }
  }

  // MARK: Validate batch size
  // --------------------------------------------------

  /**
   * Accepts only non-null, non-empty collections within the limit.
   * Element values are validated by their own constraints.
   */
  override fun isValid(value: Collection<*>?, context: ConstraintValidatorContext): Boolean {
    if (value == null) return false

    return value.isNotEmpty() && value.size <= max
  }

}

package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

@Pattern(regexp = Constants.Pattern.EMAIL, message = Constants.Validation.INVALID_EMAIL)
@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidEmail(
  val message: String = Constants.Validation.INVALID_EMAIL,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

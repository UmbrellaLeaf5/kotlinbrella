package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PositiveDoubleValidator::class])
@MustBeDocumented
annotation class ValidPositiveDouble(
  val allowZero: Boolean = false,
  val message: String = Constants.Validation.INVALID_POSITIVE_NUMBER,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

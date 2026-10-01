package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValidator::class])
@MustBeDocumented
annotation class ValidEnum(
  val values: Array<String>,
  val name: String = Constants.Validation.ENUM_FIELD,
  val message: String = Constants.Validation.INVALID_ENUM,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

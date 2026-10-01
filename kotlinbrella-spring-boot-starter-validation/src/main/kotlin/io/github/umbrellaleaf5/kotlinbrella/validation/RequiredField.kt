package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlin.reflect.KClass

@NotNull(message = Constants.Validation.REQUIRED_FIELD)
@NotBlank(message = Constants.Validation.REQUIRED_FIELD)
@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RequiredField(
  val message: String = Constants.Validation.REQUIRED_FIELD,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

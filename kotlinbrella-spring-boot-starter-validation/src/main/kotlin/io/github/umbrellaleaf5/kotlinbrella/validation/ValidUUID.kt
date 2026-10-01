package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

@Pattern(regexp = Constants.Pattern.UUID, message = Constants.Validation.INVALID_UUID)
@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidUUID(
  val message: String = Constants.Validation.INVALID_UUID,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
)

package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidBatchIdsValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidBatchIds(
  val message: String = Constants.Validation.INVALID_BATCH_IDS,
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
  val max: Int = 100,
)

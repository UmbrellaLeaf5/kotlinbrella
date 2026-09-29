package io.github.umbrellaleaf5.kotlinbrella.openapi

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrors(
  val value: Array<ApiError> = [],
)

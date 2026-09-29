package io.github.umbrellaleaf5.kotlinbrella.openapi

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiError(
  val status: Int,
  val code: String,
  val detail: String,
)

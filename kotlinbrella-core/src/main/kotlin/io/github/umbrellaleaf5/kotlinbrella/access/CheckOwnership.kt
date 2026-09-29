package io.github.umbrellaleaf5.kotlinbrella.access

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckOwnership(
  val resource: String,
  val resourceIdParam: String = "",
  val userIdParam: String = "",
  val policy: DenialPolicy = DenialPolicy.HIDE_EXISTENCE,
)

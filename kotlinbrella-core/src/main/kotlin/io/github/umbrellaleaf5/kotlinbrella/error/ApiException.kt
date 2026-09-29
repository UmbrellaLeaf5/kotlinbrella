package io.github.umbrellaleaf5.kotlinbrella.error

open class ApiException(
  val status: Int,
  val code: String,
  val publicDetail: String,
  val diagnosticDetail: String = publicDetail,
  cause: Throwable? = null,
) : RuntimeException(diagnosticDetail, cause) {

  init {
    require(status in 400..599)
    require(code.matches(Regex("[A-Z][A-Z0-9_]*")))
  }

}

package io.github.umbrellaleaf5.kotlinbrella.error

import io.github.umbrellaleaf5.kotlinbrella.Constants

open class ApiException(
  val status: Int,
  val code: String,
  val detail: String,
  cause: Throwable? = null,
) : RuntimeException(detail, cause) {

  init {
    require(status in Constants.Http.BAD_REQUEST..Constants.Http.MAX_ERROR_STATUS)
    require(code.matches(Regex(Constants.Pattern.ERROR_CODE)))
  }

}

package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.swagger.v3.oas.models.Operation
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

class KotlinbrellaErrorOperationCustomizer : OperationCustomizer {

  // MARK: Add declared errors without overwriting responses
  // --------------------------------------------------

  override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
    val annotations = AnnotatedElementUtils.findMergedAnnotation(
      handlerMethod.method,
      ApiErrors::class.java,
    )?.value?.toList() ?: emptyList()

    if (annotations.isEmpty()) return operation

    for ((status, errors) in annotations.groupBy { it.status }) {
      val httpStatus = HttpStatus.resolve(status)
        ?: throw IllegalArgumentException("Unsupported API error status: $status")
      val statusKey = status.toString()
      val responses = operation.responses
        ?: io.swagger.v3.oas.models.responses.ApiResponses().also { operation.responses = it }
      val response = responses[statusKey] ?: ApiResponse().also {
        responses.addApiResponse(statusKey, it)
      }

      if (response.description.isNullOrBlank())
        response.description = httpStatus.reasonPhrase

      val content = response.content ?: Content().also { response.content = it }
      val media = content[Constants.ApiSpec.PROBLEM_MEDIA_TYPE] ?: MediaType().also {
        content.addMediaType(Constants.ApiSpec.PROBLEM_MEDIA_TYPE, it)
      }

      if (media.schema == null)
        media.schema = Schema<Any>().`$ref`(Constants.ApiSpec.PROBLEM_REFERENCE)

      for ((index, error) in errors.withIndex()) {
        val exampleName = "${error.code}-${index + 1}"

        if (media.examples?.containsKey(exampleName) == true) continue

        media.addExamples(exampleName, Example().apply {
          summary = error.detail
          value = mapOf(
            Constants.Web.TYPE_KEY to Constants.Web.ABOUT_BLANK,
            Constants.ApiSpec.TITLE_KEY to httpStatus.reasonPhrase,
            Constants.ApiSpec.STATUS_KEY to status,
            Constants.ApiSpec.DETAIL_KEY to error.detail,
            Constants.ApiSpec.INSTANCE_KEY to Constants.ApiSpec.SAMPLE_INSTANCE,
            Constants.Web.CODE_KEY to error.code,
            Constants.Web.TRACE_ID_KEY to Constants.ApiSpec.SAMPLE_TRACE_ID,
          )
        })
      }
    }

    return operation
  }

}

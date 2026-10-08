package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiError
import io.github.umbrellaleaf5.kotlinbrella.openapi.ApiErrors
import io.github.umbrellaleaf5.kotlinbrella.openapi.status
import io.github.umbrellaleaf5.kotlinbrella.openapi.title
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

class KotlinbrellaErrorOperationCustomizer(
  private val properties: KotlinbrellaWebProperties = KotlinbrellaWebProperties(),
) : OperationCustomizer {

  // MARK: Add declared errors without overwriting responses
  // --------------------------------------------------

  /**
   * Enriches operation responses with error examples while keeping application descriptions and schemas.
   * Merges errors sharing the same HTTP status into a single response.
   * Rendering follows the configured error shape: `{error, message}` for SIMPLE,
   * RFC 9457 problem details for STANDARD.
   */
  override fun customize(operation: Operation, handlerMethod: HandlerMethod): Operation {
    val annotations = AnnotatedElementUtils.findMergedAnnotation(
      handlerMethod.method,
      ApiErrors::class.java,
    )?.value?.toList() ?: emptyList()

    if (annotations.isEmpty()) return operation

    for ((status, errors) in annotations.groupBy { it.status }) {
      val httpStatus = HttpStatus.resolve(status)
        ?: throw IllegalArgumentException("${Constants.ApiSpec.UNSUPPORTED_STATUS}: $status")

      val statusKey = status.toString()
      val responses = operation.responses
        ?: ApiResponses().also { operation.responses = it }
      val response = responses[statusKey] ?: ApiResponse().also {
        responses.addApiResponse(statusKey, it)
      }

      mergeDescription(response, errors)

      val content = response.content ?: Content().also { response.content = it }

      if (properties.errorShape == ErrorShape.SIMPLE)
        renderSimple(content, errors)

      else
        renderStandard(content, httpStatus, status, errors)
    }

    return operation
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun mergeDescription(response: ApiResponse, errors: List<ApiError>) {
    val bullets = errors.joinToString(Constants.ApiSpec.DESCRIPTION_SEPARATOR) {
      Constants.ApiSpec.BULLET_PREFIX + it.detail
    }

    response.description = if (response.description.isNullOrBlank())
      bullets

    else
      response.description + Constants.ApiSpec.DESCRIPTION_SEPARATOR + bullets
  }

  // --------------------------------------------------

  private fun renderSimple(content: Content, errors: List<ApiError>) {
    val media = content[Constants.ApiSpec.SIMPLE_MEDIA_TYPE] ?: MediaType().also {
      content.addMediaType(Constants.ApiSpec.SIMPLE_MEDIA_TYPE, it)
    }

    if (media.schema == null)
      media.schema = Schema<Any>().`$ref`(Constants.ApiSpec.SIMPLE_REFERENCE)

    if (errors.size == 1) {
      val error = errors.first()

      media.example = mapOf(
        Constants.Web.ERROR_KEY to error.title,
        Constants.ApiSpec.MESSAGE_KEY to error.detail,
      )
    }

    else {
      for (error in errors) {
        if (media.examples?.containsKey(error.detail) == true) continue

        val example = Example()
        example.summary = error.detail
        example.value = mapOf(
          Constants.Web.ERROR_KEY to error.title,
          Constants.ApiSpec.MESSAGE_KEY to error.detail,
        )

        media.addExamples(error.detail, example)
      }
    }
  }

  // --------------------------------------------------

  private fun renderStandard(
    content: Content,
    httpStatus: HttpStatus,
    status: Int,
    errors: List<ApiError>,
  ) {
    val media = content[Constants.ApiSpec.PROBLEM_MEDIA_TYPE] ?: MediaType().also {
      content.addMediaType(Constants.ApiSpec.PROBLEM_MEDIA_TYPE, it)
    }

    if (media.schema == null)
      media.schema = Schema<Any>().`$ref`(Constants.ApiSpec.PROBLEM_REFERENCE)

    if (errors.size == 1) {
      val error = errors.first()

      media.example = mapOf(
        Constants.Web.TYPE_KEY to Constants.Web.ABOUT_BLANK,
        Constants.ApiSpec.TITLE_KEY to httpStatus.reasonPhrase,
        Constants.ApiSpec.STATUS_KEY to status,
        Constants.ApiSpec.DETAIL_KEY to error.detail,
        Constants.ApiSpec.INSTANCE_KEY to Constants.ApiSpec.SAMPLE_INSTANCE,
        Constants.Web.CODE_KEY to error.code,
        Constants.Web.TRACE_ID_KEY to Constants.ApiSpec.SAMPLE_TRACE_ID,
      )
    }

    else {
      for ((index, error) in errors.withIndex()) {
        val exampleName = "${error.code}-${index + 1}"

        if (media.examples?.containsKey(exampleName) == true) continue

        val example = Example()
        example.summary = error.detail
        example.value = mapOf(
          Constants.Web.TYPE_KEY to Constants.Web.ABOUT_BLANK,
          Constants.ApiSpec.TITLE_KEY to httpStatus.reasonPhrase,
          Constants.ApiSpec.STATUS_KEY to status,
          Constants.ApiSpec.DETAIL_KEY to error.detail,
          Constants.ApiSpec.INSTANCE_KEY to Constants.ApiSpec.SAMPLE_INSTANCE,
          Constants.Web.CODE_KEY to error.code,
          Constants.Web.TRACE_ID_KEY to Constants.ApiSpec.SAMPLE_TRACE_ID,
        )

        media.addExamples(exampleName, example)
      }
    }
  }

}

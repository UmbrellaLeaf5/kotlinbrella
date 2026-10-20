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
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

class KotlinbrellaErrorOperationCustomizer(
  private val properties: KotlinbrellaWebProperties = KotlinbrellaWebProperties(),
) : OperationCustomizer {

  private val logger = LoggerFactory.getLogger(javaClass)

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

    val known = annotations.filter { error ->
      try {
        error.status
        true
      }

      catch (e: IllegalArgumentException) {
        logger.warn("Skipping ApiError with unknown code {} on {}", error.code, handlerMethod.method)

        false
      }
    }

    if (known.isEmpty()) return operation

    for ((status, errors) in known.groupBy { it.status }) {
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
    val existing = response.description
      ?.split(Constants.ApiSpec.DESCRIPTION_SEPARATOR)
      ?.toSet()
      ?: emptySet()

    val fresh = errors
      .filter { error -> (Constants.ApiSpec.BULLET_PREFIX + error.detail) !in existing }
      .joinToString(Constants.ApiSpec.DESCRIPTION_SEPARATOR) {
        Constants.ApiSpec.BULLET_PREFIX + it.detail
      }

    if (fresh.isEmpty()) return

    response.description = if (response.description.isNullOrBlank()) fresh
    else response.description + Constants.ApiSpec.DESCRIPTION_SEPARATOR + fresh
  }

  // --------------------------------------------------

  private fun renderSimple(content: Content, errors: List<ApiError>) {
    content.remove(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    val media = content[Constants.ApiSpec.SIMPLE_MEDIA_TYPE] ?: MediaType().also {
      content.addMediaType(Constants.ApiSpec.SIMPLE_MEDIA_TYPE, it)
    }

    ensureSchema(media, Constants.ApiSpec.SIMPLE_REFERENCE)

    media.example = null

    for (error in errors) {
      val exampleName = exampleNameFor(error, media)

      if (exampleName.isEmpty()) continue

      val example = Example()
      example.summary = error.detail
      example.value = mapOf(
        Constants.Web.ERROR_KEY to error.title,
        Constants.ApiSpec.MESSAGE_KEY to error.detail,
      )

      media.addExamples(exampleName, example)
    }
  }

  // --------------------------------------------------

  private fun renderStandard(
    content: Content,
    httpStatus: HttpStatus,
    status: Int,
    errors: List<ApiError>,
  ) {
    content.remove(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    val media = content[Constants.ApiSpec.PROBLEM_MEDIA_TYPE] ?: MediaType().also {
      content.addMediaType(Constants.ApiSpec.PROBLEM_MEDIA_TYPE, it)
    }

    ensureSchema(media, Constants.ApiSpec.PROBLEM_REFERENCE)

    media.example = null

    for (error in errors) {
      val exampleName = exampleNameFor(error, media)

      if (exampleName.isEmpty()) continue

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

  // --------------------------------------------------

  private fun ensureSchema(media: MediaType, expectedRef: String) {
    val actualRef = media.schema?.`$ref`

    if (actualRef == null) {
      media.schema = Schema<Any>().`$ref`(expectedRef)

      return
    }

    if (actualRef == expectedRef) return

    logger.warn("Error response keeps foreign schema {}, expected {}", actualRef, expectedRef)

    if (properties.forceErrorSchema)
      media.schema = Schema<Any>().`$ref`(expectedRef)
  }

  // --------------------------------------------------

  private fun exampleNameFor(error: ApiError, media: MediaType): String {
    var name = error.code
    var index = 2

    while (media.examples?.containsKey(name) == true) {
      val kept = media.examples[name]?.value as? Map<*, *>

      if (kept?.get(Constants.ApiSpec.MESSAGE_KEY) == error.detail) return name

      if (kept?.get(Constants.ApiSpec.DETAIL_KEY) == error.detail) return name

      name = "${error.code}-$index"
      index++
    }

    return name
  }

}

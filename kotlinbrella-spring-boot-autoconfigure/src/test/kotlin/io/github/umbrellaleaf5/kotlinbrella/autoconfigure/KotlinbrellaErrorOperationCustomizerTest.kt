package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.web.method.HandlerMethod

class KotlinbrellaErrorOperationCustomizerTest {

  private val bean = ErrorDocsFixtureController()

  // MARK: Render the simple shape
  // --------------------------------------------------

  @Test
  fun rendersSingleSimpleErrorAsExample() {
    val operation = customize(ErrorShape.SIMPLE, "single")
    val media = operation.responses["404"]?.content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    assertEquals("• Calculation not found", operation.responses["404"]?.description)
    assertEquals(Constants.ApiSpec.SIMPLE_REFERENCE, media?.schema?.`$ref`)
    assertNull(media?.example)
    assertEquals(
      mapOf("error" to "Not Found", "message" to "Calculation not found"),
      media?.examples?.get("NOT_FOUND")?.value,
    )
  }

  // --------------------------------------------------

  @Test
  fun rendersMultipleSimpleErrorsAsExamples() {
    val operation = customize(ErrorShape.SIMPLE, "multiple")
    val media = operation.responses["400"]?.content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    assertEquals("• First problem<br>• Second problem", operation.responses["400"]?.description)
    assertEquals(
      setOf("BAD_REQUEST", "INVALID_UUID"),
      media?.examples?.keys,
    )
    assertEquals(
      mapOf("error" to "Bad Request", "message" to "First problem"),
      media?.examples?.get("BAD_REQUEST")?.value,
    )
  }

  // --------------------------------------------------

  @Test
  fun keysDuplicateDetailsByCode() {
    val simple = customize(ErrorShape.SIMPLE, "duplicate")
    val standard = customize(ErrorShape.STANDARD, "duplicate")
    val simpleMedia = simple.responses["400"]?.content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)
    val standardMedia = standard.responses["400"]?.content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    assertEquals(
      setOf("BAD_REQUEST", "INVALID_UUID"),
      simpleMedia?.examples?.keys,
    )
    assertEquals(
      setOf("BAD_REQUEST", "INVALID_UUID"),
      standardMedia?.examples?.keys,
    )
  }

  // --------------------------------------------------

  @Test
  fun suffixesRepeatedCode() {
    val operation = customize(ErrorShape.SIMPLE, "sameCode")
    val media = operation.responses["400"]?.content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    assertEquals(
      setOf("BAD_REQUEST", "BAD_REQUEST-2"),
      media?.examples?.keys,
    )

    customize(ErrorShape.SIMPLE, "sameCode", operation)

    assertEquals(
      setOf("BAD_REQUEST", "BAD_REQUEST-2"),
      operation.responses["400"]?.content
        ?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)?.examples?.keys,
    )
  }

  // --------------------------------------------------

  @Test
  fun mergesSimpleBulletsIntoExistingDescription() {
    val operation = Operation().apply {
      responses = ApiResponses().apply {
        addApiResponse("404", ApiResponse().apply { description = "Existing" })
      }
    }

    customize(ErrorShape.SIMPLE, "single", operation)

    assertEquals(
      "Existing<br>• Calculation not found",
      operation.responses["404"]?.description,
    )
  }

  @Test
  fun dedupesOnlyExactBullets() {
    val operation = Operation().apply {
      responses = ApiResponses().apply {
        addApiResponse("400", ApiResponse().apply {
          description = "Mentions First problem inside"
        })
      }
    }

    customize(ErrorShape.SIMPLE, "multiple", operation)

    assertEquals(
      "Mentions First problem inside<br>• First problem<br>• Second problem",
      operation.responses["400"]?.description,
    )
  }

  // MARK: Render the standard shape
  // --------------------------------------------------

  @Test
  fun rendersSingleStandardErrorAsExample() {
    val operation = customize(ErrorShape.STANDARD, "single")
    val media = operation.responses["404"]?.content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    assertEquals("• Calculation not found", operation.responses["404"]?.description)
    assertEquals(Constants.ApiSpec.PROBLEM_REFERENCE, media?.schema?.`$ref`)
    assertNull(media?.example)
    assertEquals(
      mapOf(
        "type" to "about:blank",
        "title" to "Not Found",
        "status" to 404,
        "detail" to "Calculation not found",
        "instance" to "/example",
        "code" to "NOT_FOUND",
        "traceId" to "example-trace-id",
      ),
      media?.examples?.get("NOT_FOUND")?.value,
    )
  }

  // --------------------------------------------------

  @Test
  fun rendersMultipleStandardErrorsAsExamples() {
    val operation = customize(ErrorShape.STANDARD, "multiple")
    val media = operation.responses["400"]?.content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    assertEquals("• First problem<br>• Second problem", operation.responses["400"]?.description)
    assertEquals(
      setOf("BAD_REQUEST", "INVALID_UUID"),
      media?.examples?.keys,
    )
  }

  @Test
  fun dropsForeignMediaTypeForSimpleShape() {
    val operation = errorOperationWithMedia(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    customize(ErrorShape.SIMPLE, "single", operation)

    val content = operation.responses["404"]?.content

    assertNull(content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE))
    assertTrue(content?.containsKey(Constants.ApiSpec.SIMPLE_MEDIA_TYPE) == true)
  }

  // --------------------------------------------------

  @Test
  fun dropsForeignMediaTypeForStandardShape() {
    val operation = errorOperationWithMedia(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    customize(ErrorShape.STANDARD, "single", operation)

    val content = operation.responses["404"]?.content

    assertNull(content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE))
    assertTrue(content?.containsKey(Constants.ApiSpec.PROBLEM_MEDIA_TYPE) == true)
  }

  // MARK: Register the simple schema
  // --------------------------------------------------

  @Test
  fun registersSimpleErrorSchemaForSimpleShape() {
    val openApi = OpenAPI().apply { components = Components() }
    val properties = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.SIMPLE
    }

    KotlinbrellaSchemaCustomizer(properties).customise(openApi)

    assertEquals(
      setOf("error", "message"),
      openApi.components.schemas[Constants.ApiSpec.SIMPLE_SCHEMA]?.properties?.keys,
    )
  }

  // --------------------------------------------------

  @Test
  fun dropsForeignSchemasForSimpleShape() {
    val openApi = OpenAPI().apply { components = Components() }
    val standard = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.STANDARD
    }
    val simple = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.SIMPLE
    }

    KotlinbrellaSchemaCustomizer(standard).customise(openApi)
    KotlinbrellaSchemaCustomizer(simple).customise(openApi)

    assertEquals(
      setOf(Constants.ApiSpec.SIMPLE_SCHEMA),
      openApi.components.schemas.keys,
    )
  }

  @Test
  fun dropsSimpleSchemaWhenStandardSelected() {
    val openApi = OpenAPI().apply { components = Components() }
    val simple = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.SIMPLE
    }
    val standard = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.STANDARD
    }

    KotlinbrellaSchemaCustomizer(simple).customise(openApi)
    KotlinbrellaSchemaCustomizer(standard).customise(openApi)

    assertEquals(
      setOf(Constants.ApiSpec.PROBLEM_SCHEMA, Constants.ApiSpec.VIOLATION_SCHEMA),
      openApi.components.schemas.keys,
    )
  }

  // MARK: Keep repeated customization idempotent
  // --------------------------------------------------

  @Test
  fun repeatedCustomizationDoesNotDuplicateBullets() {
    val operation = customize(ErrorShape.SIMPLE, "multiple")

    customize(ErrorShape.SIMPLE, "multiple", operation)

    assertEquals(
      "• First problem<br>• Second problem",
      operation.responses["400"]?.description,
    )
  }

  // MARK: Leave unrelated operations alone
  // --------------------------------------------------

  @Test
  fun keepsForeignSchemaByDefault() {
    val operation = operationWithSchema("404", "#/components/schemas/Foreign")

    customize(ErrorShape.SIMPLE, "single", operation)

    assertEquals(
      "#/components/schemas/Foreign",
      operation.responses["404"]?.content
        ?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)?.schema?.`$ref`,
    )
  }

  // --------------------------------------------------

  @Test
  fun replacesForeignSchemaWhenForced() {
    val properties = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.SIMPLE
      forceErrorSchema = true
    }
    val bean = ErrorDocsFixtureController()
    val method = ErrorDocsFixtureController::class.java.getMethod("single")
    val operation = operationWithSchema("404", "#/components/schemas/Foreign")

    KotlinbrellaErrorOperationCustomizer(properties)
      .customize(operation, HandlerMethod(bean, method))

    assertEquals(
      Constants.ApiSpec.SIMPLE_REFERENCE,
      operation.responses["404"]?.content
        ?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)?.schema?.`$ref`,
    )
  }

  @Test
  fun skipsUnknownCodesWithoutFailing() {
    val bean = ErrorDocsBadCodeFixture()
    val method = ErrorDocsBadCodeFixture::class.java.getMethod("broken")
    val properties = KotlinbrellaWebProperties().apply {
      errorShape = ErrorShape.SIMPLE
    }
    val operation = KotlinbrellaErrorOperationCustomizer(properties)
      .customize(Operation(), HandlerMethod(bean, method))

    assertNull(operation.responses)
  }

  // --------------------------------------------------

  @Test
  fun leavesOperationsWithoutAnnotationsUntouched() {
    val operation = Operation()

    val customized = customize(ErrorShape.SIMPLE, "plain", operation)

    assertTrue(customized === operation)
    assertNull(customized.responses)
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun customize(
    shape: ErrorShape,
    methodName: String,
    operation: Operation = Operation(),
  ): Operation {
    val properties = KotlinbrellaWebProperties().apply {
      errorShape = shape
    }
    val method = ErrorDocsFixtureController::class.java.getMethod(methodName)
    val handlerMethod = HandlerMethod(bean, method)

    return KotlinbrellaErrorOperationCustomizer(properties).customize(operation, handlerMethod)
  }

  // --------------------------------------------------

  private fun errorOperationWithMedia(mediaType: String): Operation {
    val operation = Operation()
    val responses = ApiResponses()
    val response = ApiResponse()
    val content = Content()

    content.addMediaType(mediaType, MediaType())
    response.content = content
    responses.addApiResponse("404", response)
    operation.responses = responses

    return operation
  }

  // --------------------------------------------------

  private fun operationWithSchema(status: String, ref: String): Operation {
    val operation = Operation()
    val responses = ApiResponses()
    val response = ApiResponse()
    val content = Content()
    val media = MediaType()

    media.schema = Schema<Any>().`$ref`(ref)
    content.addMediaType(Constants.ApiSpec.SIMPLE_MEDIA_TYPE, media)
    response.content = content
    responses.addApiResponse(status, response)
    operation.responses = responses

    return operation
  }

}

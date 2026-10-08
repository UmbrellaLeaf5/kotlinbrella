package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
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
    assertEquals(
      mapOf("error" to "Not Found", "message" to "Calculation not found"),
      media?.example,
    )
  }

  // --------------------------------------------------

  @Test
  fun rendersMultipleSimpleErrorsAsExamples() {
    val operation = customize(ErrorShape.SIMPLE, "multiple")
    val media = operation.responses["400"]?.content?.get(Constants.ApiSpec.SIMPLE_MEDIA_TYPE)

    assertEquals("• First problem<br>• Second problem", operation.responses["400"]?.description)
    assertEquals(
      setOf("First problem", "Second problem"),
      media?.examples?.keys,
    )
    assertEquals(
      mapOf("error" to "Bad Request", "message" to "First problem"),
      media?.examples?.get("First problem")?.value,
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

  // MARK: Render the standard shape
  // --------------------------------------------------

  @Test
  fun rendersSingleStandardErrorAsExample() {
    val operation = customize(ErrorShape.STANDARD, "single")
    val media = operation.responses["404"]?.content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    assertEquals("• Calculation not found", operation.responses["404"]?.description)
    assertEquals(Constants.ApiSpec.PROBLEM_REFERENCE, media?.schema?.`$ref`)
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
      media?.example,
    )
  }

  // --------------------------------------------------

  @Test
  fun rendersMultipleStandardErrorsAsExamples() {
    val operation = customize(ErrorShape.STANDARD, "multiple")
    val media = operation.responses["400"]?.content?.get(Constants.ApiSpec.PROBLEM_MEDIA_TYPE)

    assertEquals("• First problem<br>• Second problem", operation.responses["400"]?.description)
    assertEquals(
      setOf("BAD_REQUEST-1", "INVALID_UUID-2"),
      media?.examples?.keys,
    )
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

  // MARK: Leave unrelated operations alone
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

}

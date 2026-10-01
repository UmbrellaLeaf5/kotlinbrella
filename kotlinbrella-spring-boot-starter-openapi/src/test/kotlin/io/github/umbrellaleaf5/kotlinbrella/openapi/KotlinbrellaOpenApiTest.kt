package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.Constants
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.ErrorViolationMapper
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.KotlinbrellaErrorAdvice
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.KotlinbrellaErrorOperationCustomizer
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.KotlinbrellaSchemaCustomizer
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.KotlinbrellaWebProperties
import io.github.umbrellaleaf5.kotlinbrella.util.checkNotNullByName
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.method.HandlerMethod

class KotlinbrellaOpenApiTest {

  // MARK: Preserve existing responses and add distinct examples
  // --------------------------------------------------

  @Test
  fun documentsMultipleCodesWithoutClobberingDeclaredResponses() {
    val existing = ApiResponse().description("Existing description")
      .content(Content().addMediaType("application/json", MediaType()))
    val operation = Operation().responses(ApiResponses().addApiResponse("400", existing))
    val method = OpenApiFixture::class.java.getMethod("documented")

    KotlinbrellaErrorOperationCustomizer()
      .customize(operation, HandlerMethod(OpenApiFixture(), method))

    val response = operation.responses["400"].checkNotNullByName { "response" }
    val media = response.content[Constants.ApiSpec.PROBLEM_MEDIA_TYPE]
      .checkNotNullByName { "problemMedia" }

    assertEquals("Existing description", response.description)
    assertNotNull(response.content["application/json"])
    assertEquals(Constants.ApiSpec.PROBLEM_REFERENCE, media.schema.`$ref`)
    assertEquals(setOf("BAD_REQUEST-1", "INVALID_UUID-2"), media.examples.keys)
    assertEquals("Not Found", operation.responses["404"]?.description)
  }

  // --------------------------------------------------

  @Test
  fun discoversAnnotationsDeclaredOnApiSpecInterfaces() {
    val method = OpenApiFixtureImplementation::class.java.getMethod("documented")
    val operation = KotlinbrellaErrorOperationCustomizer()
      .customize(Operation(), HandlerMethod(OpenApiFixtureImplementation(), method))

    assertNotNull(operation.responses?.get("403"))
  }

  // --------------------------------------------------

  @Test
  fun registersRuntimeProblemFieldsOnce() {
    val specification = OpenAPI()
    val customizer = KotlinbrellaSchemaCustomizer()
    customizer.customise(specification)
    customizer.customise(specification)

    val schemas = specification.components.schemas
    val problem = schemas[Constants.ApiSpec.PROBLEM_SCHEMA]
      .checkNotNullByName { "problemSchema" }
    val violation = schemas[Constants.ApiSpec.VIOLATION_SCHEMA]
      .checkNotNullByName { "violationSchema" }

    assertEquals(2, schemas.size)
    assertTrue(problem.properties.keys.containsAll(setOf(
      "type", "title", "status", "detail", "instance", "code", "traceId", "violations",
    )))
    assertEquals(setOf("field", "message", "code"), violation.properties.keys)
  }

  // --------------------------------------------------

  @Test
  fun documentedFieldsMatchMvcProblemResponse() {
    val specification = OpenAPI()
    KotlinbrellaSchemaCustomizer().customise(specification)
    val schema = specification.components.schemas[Constants.ApiSpec.PROBLEM_SCHEMA]
      .checkNotNullByName { "problemSchema" }
    val specificationJson = Json.pretty(specification)
    val mvc = MockMvcBuilders.standaloneSetup(OpenApiFixtureController())
      .setControllerAdvice(
        KotlinbrellaErrorAdvice(
          KotlinbrellaWebProperties(),
          MockEnvironment(),
          ErrorViolationMapper(),
        ),
      )
      .build()
    val response = mvc.perform(get("/problem")).andReturn().response

    assertEquals(400, response.status)
    assertTrue(response.contentType.orEmpty().startsWith(Constants.ApiSpec.PROBLEM_MEDIA_TYPE))

    for (field in schema.properties.keys - Constants.Web.VIOLATIONS_KEY)
      assertTrue(response.contentAsString.contains("\"$field\""),
        "Missing runtime field: $field in ${response.contentAsString}")

    assertTrue(specificationJson.contains(Constants.ApiSpec.PROBLEM_SCHEMA))
    assertTrue(specificationJson.contains(Constants.Web.TRACE_ID_KEY))
  }

}

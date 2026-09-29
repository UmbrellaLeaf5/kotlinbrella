package io.github.umbrellaleaf5.kotlinbrella.samples.full

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class FullMvcApplicationTest(
  // services:
  @param:Autowired private val mvc: MockMvc,
) {

  // MARK: Exercise creation, lookup and ownership through MVC
  // --------------------------------------------------

  @Test
  fun createsAndReadsOwnedItemButConcealsForeignOwner() {
    val ownerId = UUID.randomUUID()
    val name = "sample"
    val created = mvc.perform(post(Constants.Api.ITEM_PATH)
      .param(Constants.Api.USER_ID, ownerId.toString())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"${Constants.Json.NAME}\":\"$name\"}"))
      .andReturn().response

    assertEquals(HttpStatus.OK.value(), created.status)

    val itemId = created.contentAsString
      .substringAfter("\"${Constants.Json.ID}\":\"").substringBefore('"')
    val owned = mvc.perform(get(Constants.Api.ITEM_BY_ID_PATH, itemId)
      .param(Constants.Api.USER_ID, ownerId.toString())).andReturn().response
    val foreign = mvc.perform(get(Constants.Api.ITEM_BY_ID_PATH, itemId)
      .param(Constants.Api.USER_ID, UUID.randomUUID().toString())).andReturn().response

    assertEquals(HttpStatus.OK.value(), owned.status)
    assertEquals(HttpStatus.NOT_FOUND.value(), foreign.status)
    assertTrue(owned.contentAsString.contains(name))
  }

  // --------------------------------------------------

  @Test
  fun rendersValidationAndConversionFailures() {
    val invalidPatch = mvc.perform(post(Constants.Api.ITEM_PATH)
      .param(Constants.Api.USER_ID, UUID.randomUUID().toString())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{}"))
      .andReturn().response
    val malformedId = mvc.perform(get(Constants.Api.ITEM_BY_ID_PATH, "not-a-uuid")
      .param(Constants.Api.USER_ID, UUID.randomUUID().toString())).andReturn().response

    assertEquals(HttpStatus.BAD_REQUEST.value(), invalidPatch.status)
    assertTrue(invalidPatch.contentAsString.contains(ErrorCode.AT_LEAST_ONE_PRESENT))
    assertEquals(HttpStatus.BAD_REQUEST.value(), malformedId.status)
    assertTrue(malformedId.contentAsString.contains(ErrorCode.INVALID_UUID))
  }

  // --------------------------------------------------

  @Test
  fun includesCanonicalErrorSchemasInGeneratedSpec() {
    val response = mvc.perform(get(Constants.Api.OPENAPI_PATH)).andReturn().response

    assertEquals(HttpStatus.OK.value(), response.status)
    assertTrue(response.contentAsString.contains("KotlinbrellaProblem"))
    assertTrue(response.contentAsString.contains(ErrorCode.AT_LEAST_ONE_PRESENT))
  }

  companion object {

    @Container
    @JvmStatic
    val postgres = PostgreSQLContainer("postgres:16-alpine")
      .withDatabaseName("sample")
      .withUsername("sample")
      .withPassword("sample")

    @DynamicPropertySource
    @JvmStatic
    fun configureDatabase(registry: DynamicPropertyRegistry) {
      registry.add("SAMPLE_DATABASE_URL", postgres::getJdbcUrl)
      registry.add("SAMPLE_DATABASE_USER", postgres::getUsername)
      registry.add("SAMPLE_DATABASE_PASSWORD", postgres::getPassword)
    }

  }

}

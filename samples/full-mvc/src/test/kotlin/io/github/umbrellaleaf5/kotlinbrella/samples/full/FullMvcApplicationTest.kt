package io.github.umbrellaleaf5.kotlinbrella.samples.full

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
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
    val created = mvc.perform(post("/api/item")
      .param("user_id", ownerId.toString())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\"name\":\"sample\"}"))
      .andReturn().response

    assertEquals(200, created.status)

    val itemId = created.contentAsString.substringAfter("\"id\":\"").substringBefore('"')
    val owned = mvc.perform(get("/api/item/$itemId")
      .param("user_id", ownerId.toString())).andReturn().response
    val foreign = mvc.perform(get("/api/item/$itemId")
      .param("user_id", UUID.randomUUID().toString())).andReturn().response

    assertEquals(200, owned.status)
    assertEquals(404, foreign.status)
    assertTrue(owned.contentAsString.contains("sample"))
  }

  // --------------------------------------------------

  @Test
  fun rendersValidationAndConversionFailures() {
    val invalidPatch = mvc.perform(post("/api/item")
      .param("user_id", UUID.randomUUID().toString())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{}"))
      .andReturn().response
    val malformedId = mvc.perform(get("/api/item/not-a-uuid")
      .param("user_id", UUID.randomUUID().toString())).andReturn().response

    assertEquals(400, invalidPatch.status)
    assertTrue(invalidPatch.contentAsString.contains("AT_LEAST_ONE_PRESENT"))
    assertEquals(400, malformedId.status)
    assertTrue(malformedId.contentAsString.contains("INVALID_UUID"))
  }

  // --------------------------------------------------

  @Test
  fun includesCanonicalErrorSchemasInGeneratedSpec() {
    val response = mvc.perform(get("/v3/api-docs")).andReturn().response

    assertEquals(200, response.status)
    assertTrue(response.contentAsString.contains("KotlinbrellaProblem"))
    assertTrue(response.contentAsString.contains("AT_LEAST_ONE_PRESENT"))
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

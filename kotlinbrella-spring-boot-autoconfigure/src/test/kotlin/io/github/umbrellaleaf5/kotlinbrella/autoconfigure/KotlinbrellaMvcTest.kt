package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class KotlinbrellaMvcTest {

  private val mockMvc = MockMvcBuilders.standaloneSetup(WebFixtureController())
    .setControllerAdvice(
      KotlinbrellaErrorAdvice(
        KotlinbrellaWebProperties(),
        ErrorViolationMapper(),
      ),
    )
    .build()

  // MARK: Render canonical error body over HTTP
  // --------------------------------------------------

  @Test
  fun rendersProblemJsonAndTraceId() {
    val response = mockMvc.perform(get("/failure")).andReturn().response

    assertEquals(400, response.status)
    assertTrue(response.contentType.orEmpty().startsWith("application/problem+json"))
    assertTrue(response.contentAsString.contains("\"detail\":\"Rejected input\""))
    assertTrue(response.contentAsString.contains("\"code\":\"BAD_REQUEST\""))
    assertTrue(response.contentAsString.contains("\"traceId\""))
  }

  // --------------------------------------------------

  @Test
  fun mapsMissingParameterToBadRequest() {
    val response = mockMvc.perform(get("/probe")).andReturn().response

    assertEquals(400, response.status)
    assertFalse(response.contentAsString.contains("valueString"))
  }

  // --------------------------------------------------

  @Test
  fun mapsMethodValidationFailureToBadRequest() {
    val empty = mockMvc.perform(
      post("/batch").contentType(MediaType.APPLICATION_JSON).content("[]")
    ).andReturn().response

    assertEquals(400, empty.status)
    assertTrue(empty.contentAsString.contains("\"code\":\"BAD_REQUEST\""))

    val oversized = mockMvc.perform(
      post("/batch").contentType(MediaType.APPLICATION_JSON)
        .content(List(101) { "\"id-$it\"" }.joinToString(",", "[", "]"))
    ).andReturn().response

    assertEquals(400, oversized.status)
  }

  // --------------------------------------------------

  @Test
  fun preservesMethodAndMediaTypeStatuses() {
    val method = mockMvc.perform(put("/probe")).andReturn().response
    val media = mockMvc.perform(post("/probe").contentType("text/plain"))
      .andReturn().response

    assertEquals(405, method.status)
    assertEquals(415, media.status)
  }

}

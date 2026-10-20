package io.github.umbrellaleaf5.kotlinbrella.test

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

object ErrorDocsParity {

  private val mapper = ObjectMapper()

  // MARK: Assert documented error examples match runtime
  // --------------------------------------------------

  /**
   * Asserts every documented error example matches
   * the real MVC response body field by field.
   *
   * @param mockMvc MVC entry point of the consuming application
   * @param openApiJson raw `/v3/api-docs` response body
   * @param method HTTP method (`get`, `post`, `delete`, ...)
   * @param path OpenAPI path template (`/api/v1/calculation/{id}/status`)
   * @param probes expected status to a probe URL returning it
   * @param strict when `true`, unexpected runtime fields fail the assertion
   */
  fun assertParity(
    mockMvc: MockMvc,
    openApiJson: String,
    method: String,
    path: String,
    probes: Map<Int, String>,
    strict: Boolean = true,
  ) {
    val responses = mapper.readTree(openApiJson)
      .path("paths").path(path).path(method.lowercase()).path("responses")

    for ((status, probeUrl) in probes) {
      val documented = documentedExamples(responses, method, path, status)

      val body = mapper.readTree(
        mockMvc.perform(requestFor(method, probeUrl)).andReturn().response.contentAsString
      )

      for (example in documented) {
        for ((key, value) in example.properties()) {
          assertEquals(
            value.toString(),
            body.path(key).toString(),
            "Field $key mismatch for $method $path $status",
          )
        }

        if (strict) assertNoExtraFields(example, body, method, path, status)
      }
    }
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun requestFor(method: String, url: String): MockHttpServletRequestBuilder =
    when (method.lowercase()) {
      "get" -> get(url)
      "post" -> post(url)
      "put" -> put(url)
      "patch" -> patch(url)
      "delete" -> delete(url)
      else -> throw IllegalArgumentException("Unsupported HTTP method: $method")
    }

  // --------------------------------------------------

  private fun assertNoExtraFields(
    example: JsonNode,
    body: JsonNode,
    method: String,
    path: String,
    status: Int,
  ) {
    for ((key, _) in body.properties()) {
      assertTrue(
        example.has(key),
        "Unexpected runtime field $key for $method $path $status",
      )
    }
  }

  // --------------------------------------------------

  private fun documentedExamples(
    responses: JsonNode,
    method: String,
    path: String,
    status: Int,
  ): List<JsonNode> {
    val response = responses.path(status.toString())

    assertTrue(
      !response.isMissingNode,
      "No documented response $status for $method $path",
    )

    val documented = mutableListOf<JsonNode>()

    for ((_, media) in response.path("content").properties()) {
      if (media.has("example") && !media.path("example").isNull)
        documented.add(media.path("example"))

      val examples = media.path("examples")

      if (examples.isObject) {
        for ((_, example) in examples.properties())
          documented.add(example.path("value"))
      }
    }

    assertTrue(
      documented.isNotEmpty(),
      "No documented error example for $method $path $status",
    )

    return documented
  }

}

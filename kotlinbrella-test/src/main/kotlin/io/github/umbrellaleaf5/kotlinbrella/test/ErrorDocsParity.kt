package io.github.umbrellaleaf5.kotlinbrella.test

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

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
   * @param method lowercase HTTP method (`get`, `delete`, ...)
   * @param path OpenAPI path template (`/api/v1/calculation/{id}/status`)
   * @param probes expected status to a probe URL returning it
   */
  fun assertParity(
    mockMvc: MockMvc,
    openApiJson: String,
    method: String,
    path: String,
    probes: Map<Int, String>,
  ) {
    val responses = mapper.readTree(openApiJson)
      .path("paths").path(path).path(method.lowercase()).path("responses")

    for ((status, probeUrl) in probes) {
      val documented = documentedExamples(responses, method, path, status)

      val body = mapper.readTree(
        mockMvc.perform(get(probeUrl)).andReturn().response.contentAsString
      )

      for (example in documented) {
        val fields = example.fields()

        while (fields.hasNext()) {
          val (key, value) = fields.next()

          assertEquals(
            value.asText(),
            body.path(key).asText(),
            "Field $key mismatch for $method $path $status",
          )
        }
      }
    }
  }

  // MARK: Private Helpers
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
    val contents = response.path("content").fields()

    while (contents.hasNext()) {
      val media = contents.next().value

      if (media.has("example") && !media.path("example").isNull)
        documented.add(media.path("example"))

      val examples = media.path("examples")

      if (examples.isObject) {
        val values = examples.fields()

        while (values.hasNext())
          documented.add(values.next().value.path("value"))
      }
    }

    assertTrue(
      documented.isNotEmpty(),
      "No documented error example for $method $path $status",
    )

    return documented
  }

}

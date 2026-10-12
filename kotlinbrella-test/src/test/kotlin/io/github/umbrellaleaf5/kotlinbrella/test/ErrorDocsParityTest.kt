package io.github.umbrellaleaf5.kotlinbrella.test

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ErrorDocsParityTest {

  private val mockMvc = MockMvcBuilders.standaloneSetup(ParityFixtureController()).build()

  // MARK: Match documented examples against runtime
  // --------------------------------------------------

  @Test
  fun acceptsMatchingDocs() {
    ErrorDocsParity.assertParity(
      mockMvc = mockMvc,
      openApiJson = SPEC,
      method = "get",
      path = "/probe",
      probes = mapOf(404 to "/probe"),
    )
  }

  // --------------------------------------------------

  @Test
  fun rejectsMismatchedDocs() {
    val drifted = SPEC.replace("Gone", "Vanished")

    assertThrows(AssertionError::class.java) {
      ErrorDocsParity.assertParity(
        mockMvc = mockMvc,
        openApiJson = drifted,
        method = "get",
        path = "/probe",
        probes = mapOf(404 to "/probe"),
      )
    }
  }

  companion object {

    private const val SPEC = """
      {
        "paths": {
          "/probe": {
            "get": {
              "responses": {
                "404": {
                  "content": {
                    "application/json": {
                      "examples": {
                        "NOT_FOUND": {
                          "value": {
                            "error": "Not Found",
                            "message": "Gone"
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    """

  }

}

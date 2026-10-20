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
      openApiJson = spec("get"),
      method = "get",
      path = "/probe",
      probes = mapOf(404 to "/probe"),
    )
  }

  // --------------------------------------------------

  @Test
  fun probesWithDocumentedMethod() {
    ErrorDocsParity.assertParity(
      mockMvc = mockMvc,
      openApiJson = spec("post"),
      method = "post",
      path = "/probe",
      probes = mapOf(404 to "/probe"),
    )
  }

  // --------------------------------------------------

  @Test
  fun rejectsMismatchedDocs() {
    val drifted = spec("get").replace("Gone", "Vanished")

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

  // --------------------------------------------------

  @Test
  fun rejectsTypeDrift() {
    assertThrows(AssertionError::class.java) {
      ErrorDocsParity.assertParity(
        mockMvc = mockMvc,
        openApiJson = TYPED_SPEC,
        method = "get",
        path = "/typed",
        probes = mapOf(404 to "/typed"),
      )
    }
  }

  // --------------------------------------------------

  @Test
  fun rejectsUnexpectedRuntimeFields() {
    assertThrows(AssertionError::class.java) {
      ErrorDocsParity.assertParity(
        mockMvc = mockMvc,
        openApiJson = spec("get", "/extra"),
        method = "get",
        path = "/extra",
        probes = mapOf(404 to "/extra"),
      )
    }
  }

  // --------------------------------------------------

  @Test
  fun allowsUnexpectedRuntimeFieldsWhenNotStrict() {
    ErrorDocsParity.assertParity(
      mockMvc = mockMvc,
      openApiJson = spec("get", "/extra"),
      method = "get",
      path = "/extra",
      probes = mapOf(404 to "/extra"),
      strict = false,
    )
  }

  companion object {

    private const val TYPED_SPEC = """
      {
        "paths": {
          "/typed": {
            "get": {
              "responses": {
                "404": {
                  "content": {
                    "application/json": {
                      "examples": {
                        "NOT_FOUND": {
                          "value": {
                            "code": "404"
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

    // --------------------------------------------------

    private fun spec(
      method: String,
      path: String = "/probe",
    ): String = """
      {
        "paths": {
          "$path": {
            "$method": {
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

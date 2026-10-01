package io.github.umbrellaleaf5.kotlinbrella.error

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ApiExceptionTest {

  // MARK: Preserve status and diagnostics
  // --------------------------------------------------

  @Test
  fun preservesSeparateDetailsAndCause() {
    val cause = IllegalStateException("database detail")
    val exception = ConflictException("diagnostic", "safe", cause = cause)

    assertEquals(409, exception.status)
    assertEquals(ErrorCode.CONFLICT, exception.code)
    assertEquals("safe", exception.publicDetail)
    assertEquals("diagnostic", exception.diagnosticDetail)
    assertSame(cause, exception.cause)
    assertEquals("diagnostic", exception.message)
  }

  // --------------------------------------------------

  @Test
  fun unifiedFactoriesKeepIdenticalMessages() {
    val exceptions = listOf(
      BadRequestException.unified("bad"),
      NotFoundException.unified("missing"),
      ConflictException.unified("conflict"),
      ForbiddenException.unified("forbidden"),
    )

    assertEquals(listOf(400, 404, 409, 403), exceptions.map { it.status })
    assertEquals(exceptions.map { it.publicDetail }, exceptions.map { it.diagnosticDetail })
  }

  // --------------------------------------------------

  @Test
  fun resolvesHttpStatusFromErrorCode() {
    assertEquals(400, ErrorCode.status(ErrorCode.BAD_REQUEST))
    assertEquals(400, ErrorCode.status(ErrorCode.INVALID_UUID))
    assertEquals(404, ErrorCode.status(ErrorCode.NOT_FOUND))
    assertEquals(409, ErrorCode.status(ErrorCode.CONFLICT))
    assertEquals(413, ErrorCode.status(ErrorCode.PAYLOAD_TOO_LARGE))
    assertEquals(503, ErrorCode.status(ErrorCode.SERVICE_UNAVAILABLE))

    assertThrows(IllegalArgumentException::class.java) {
      ErrorCode.status("UNKNOWN_CODE")
    }
  }

  // --------------------------------------------------

  @Test
  fun resolvesTitleFromErrorCode() {
    assertEquals("Bad Request", ErrorCode.title(ErrorCode.BAD_REQUEST))
    assertEquals("Bad Request", ErrorCode.title(ErrorCode.INVALID_UUID))
    assertEquals("Not Found", ErrorCode.title(ErrorCode.NOT_FOUND))
    assertEquals("Conflict", ErrorCode.title(ErrorCode.CONFLICT))
    assertEquals("Payload Too Large", ErrorCode.title(ErrorCode.PAYLOAD_TOO_LARGE))
    assertEquals("Service Unavailable", ErrorCode.title(ErrorCode.SERVICE_UNAVAILABLE))

    assertThrows(IllegalArgumentException::class.java) {
      ErrorCode.title("UNKNOWN_CODE")
    }
  }

  // --------------------------------------------------

  @Test
  fun rejectsInvalidCodesAndStatuses() {
    assertThrows(IllegalArgumentException::class.java) {
      ApiException(200, ErrorCode.BAD_REQUEST, "invalid")
    }
    assertThrows(IllegalArgumentException::class.java) {
      ApiException(400, "not_uppercase", "invalid")
    }
  }

}

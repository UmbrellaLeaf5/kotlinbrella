package io.github.umbrellaleaf5.kotlinbrella.error

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ApiExceptionTest {

  // MARK: Preserve status, detail and cause
  // --------------------------------------------------

  @Test
  fun preservesDetailAndCause() {
    val cause = IllegalStateException("database detail")
    val exception = ConflictException("conflict detail", cause = cause)

    assertEquals(409, exception.status)
    assertEquals(ErrorCode.CONFLICT, exception.code)
    assertEquals("conflict detail", exception.detail)
    assertEquals("conflict detail", exception.message)
    assertSame(cause, exception.cause)
  }

  // --------------------------------------------------

  @Test
  fun subclassesCarryTheirStatusesAndCodes() {
    val exceptions = listOf(
      BadRequestException("bad"),
      NotFoundException("missing"),
      ConflictException("conflict"),
      ForbiddenException("forbidden"),
      PayloadTooLargeException("too large"),
      ServiceUnavailableException("unavailable"),
      InternalServerException("failure"),
    )

    assertEquals(listOf(400, 404, 409, 403, 413, 503, 500), exceptions.map { it.status })
    assertEquals(
      listOf(
        ErrorCode.BAD_REQUEST,
        ErrorCode.NOT_FOUND,
        ErrorCode.CONFLICT,
        ErrorCode.FORBIDDEN,
        ErrorCode.PAYLOAD_TOO_LARGE,
        ErrorCode.SERVICE_UNAVAILABLE,
        ErrorCode.INTERNAL_ERROR,
      ),
      exceptions.map { it.code },
    )
    assertEquals(exceptions.map { it.detail }, exceptions.map { it.message })
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

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
  fun rejectsInvalidCodesAndStatuses() {
    assertThrows(IllegalArgumentException::class.java) {
      ApiException(200, ErrorCode.BAD_REQUEST, "invalid")
    }
    assertThrows(IllegalArgumentException::class.java) {
      ApiException(400, "not_uppercase", "invalid")
    }
  }

}

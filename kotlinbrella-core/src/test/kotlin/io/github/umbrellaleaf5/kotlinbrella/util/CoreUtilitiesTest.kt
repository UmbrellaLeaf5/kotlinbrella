package io.github.umbrellaleaf5.kotlinbrella.util

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class CoreUtilitiesTest {

  // MARK: Validate null input and state separately
  // --------------------------------------------------

  @Test
  fun namedChecksAreLazyAndPreserveExceptionSemantics() {
    val present: String? = "value"
    assertEquals("value", present.requireNotNullByName { error("evaluated") })
    assertEquals("value", present.checkFieldNotNullByName { error("evaluated") })

    val absent: String? = null
    assertEquals("id must not be null", assertThrows(IllegalArgumentException::class.java) {
      absent.requireNotNullByName { "id" }
    }.message)
    assertEquals("Field id must not be null", assertThrows(IllegalArgumentException::class.java) {
      absent.requireFieldNotNullByName { "id" }
    }.message)
    assertThrows(IllegalStateException::class.java) {
      absent.checkNotNullByName { "id" }
    }
    assertThrows(IllegalStateException::class.java) {
      absent.checkFieldNotNullByName { "id" }
    }
  }

  // --------------------------------------------------

  @Test
  fun parsesStrictClientTypes() {
    val uuid = UUID.randomUUID()
    assertEquals(uuid, uuid.toString().toUUIDOrThrow())
    assertEquals(42, "42".toIntOrThrow())
    assertEquals(42L, "42".toLongOrThrow())
    assertEquals(1.5, "1.5".toDoubleOrThrow())
    assertEquals(Instant.EPOCH, "1970-01-01T00:00:00Z".toInstantOrThrow())
    assertEquals(Thread.State.RUNNABLE, "RUNNABLE".toEnumOrThrow<Thread.State>())
  }

  // --------------------------------------------------

  @Test
  fun rejectsMalformedAndOverflowInputs() {
    val invalid = listOf(
      "x".let { runCatching { it.toUUIDOrThrow() }.exceptionOrNull() },
      "2147483648".let { runCatching { it.toIntOrThrow() }.exceptionOrNull() },
      "9223372036854775808".let { runCatching { it.toLongOrThrow() }.exceptionOrNull() },
      "NaN".let { runCatching { it.toDoubleOrThrow() }.exceptionOrNull() },
      "yesterday".let { runCatching { it.toInstantOrThrow() }.exceptionOrNull() },
      "runnable".let {
        runCatching { it.toEnumOrThrow<Thread.State>() }.exceptionOrNull()
      },
    )

    assertEquals(
      listOf(
        ErrorCode.INVALID_UUID,
        ErrorCode.INVALID_INTEGER,
        ErrorCode.INVALID_LONG,
        ErrorCode.INVALID_DOUBLE,
        ErrorCode.INVALID_INSTANT,
        ErrorCode.INVALID_ENUM,
      ),
      invalid.map { (it as BadRequestException).code },
    )
  }

}

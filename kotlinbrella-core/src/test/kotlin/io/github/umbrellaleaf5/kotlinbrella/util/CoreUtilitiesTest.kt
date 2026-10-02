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
    assertEquals("id cannot be null", assertThrows(IllegalArgumentException::class.java) {
      absent.requireNotNullByName { "id" }
    }.message)
    assertEquals("String.id cannot be null", assertThrows(IllegalArgumentException::class.java) {
      absent.requireFieldNotNullByName { "id" }
    }.message)
    assertEquals("id cannot be null", assertThrows(IllegalStateException::class.java) {
      absent.checkNotNullByName { "id" }
    }.message)
    assertEquals("String.id cannot be null", assertThrows(IllegalStateException::class.java) {
      absent.checkFieldNotNullByName { "id" }
    }.message)
  }

  // --------------------------------------------------

  @Test
  fun objectScopedChecksIncludeObjectIdentity() {
    val objectId = UUID.randomUUID()
    val present: String? = "value"

    assertEquals("value", present.requireNotNullByNameInObject("field", "User", objectId))

    val absent: String? = null
    assertEquals(
      "field cannot be null in User: $objectId",
      assertThrows(IllegalArgumentException::class.java) {
        absent.requireNotNullByNameInObject("field", "User", objectId)
      }.message,
    )
    assertEquals(
      "String.id cannot be null in User: $objectId",
      assertThrows(IllegalStateException::class.java) {
        absent.checkFieldNotNullByNameInObject("id", "User", objectId)
      }.message,
    )
    assertEquals(
      "count cannot be null in Order: 42",
      assertThrows(IllegalArgumentException::class.java) {
        absent.requireNotNullByNameInObject("count", "Order", 42L)
      }.message,
    )
    assertEquals(
      "token cannot be null in Session: abc",
      assertThrows(IllegalStateException::class.java) {
        absent.checkNotNullByNameInObject("token", "Session", "abc")
      }.message,
    )
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
  fun nullableParsingReturnsNullInsteadOfThrowing() {
    val uuid = UUID.randomUUID()
    assertEquals(uuid, uuid.toString().toUUIDOrNull())
    assertEquals(42, "42".toIntOrNull())
    assertEquals(42L, "42".toLongOrNull())
    assertEquals(1.5, "1.5".toDoubleOrNull())
    assertEquals(Instant.EPOCH, "1970-01-01T00:00:00Z".toInstantOrNull())
    assertEquals(Thread.State.RUNNABLE, "RUNNABLE".toEnumOrNull<Thread.State>())

    val missing: String? = null
    assertEquals(null, missing.toUUIDOrNull())
    assertEquals(null, missing.toIntOrNull())
    assertEquals(null, missing.toLongOrNull())
    assertEquals(null, missing.toDoubleOrNull())
    assertEquals(null, missing.toInstantOrNull())
    assertEquals(null, missing.toEnumOrNull<Thread.State>())

    assertEquals(null, "x".toUUIDOrNull())
    assertEquals(null, "2147483648".toIntOrNull())
    assertEquals(null, "9223372036854775808".toLongOrNull())
    assertEquals(null, "NaN".toDoubleOrNull())
    assertEquals(null, "Infinity".toDoubleOrNull())
    assertEquals(null, "yesterday".toInstantOrNull())
    assertEquals(null, "runnable".toEnumOrNull<Thread.State>())
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

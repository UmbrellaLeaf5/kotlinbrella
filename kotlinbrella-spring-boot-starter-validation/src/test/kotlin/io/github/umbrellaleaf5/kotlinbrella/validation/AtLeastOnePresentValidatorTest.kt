package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Validation
import jakarta.validation.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AtLeastOnePresentValidatorTest {

  private val validator = Validation.buildDefaultValidatorFactory().validator

  // MARK: Reject empty patch regardless of technical fields
  // --------------------------------------------------

  @Test
  fun rejectsEmptyPatchAndBlankValues() {
    val empty = validator.validate(PatchInput())

    assertEquals(1, empty.size)
    assertEquals("At least one field must be provided", empty.single().message)
    assertEquals("", empty.single().propertyPath.toString())
    assertEquals(1, validator.validate(PatchInput(name = "")).size)
    assertEquals(1, validator.validate(PatchInput(
      name = " \t",
      tags = emptyList(),
      metadata = emptyMap(),
      items = emptyArray(),
    )).size)
  }

  // --------------------------------------------------

  @Test
  fun acceptsZeroFalseAndNonemptyCollections() {
    assertTrue(validator.validate(PatchInput(count = 0)).isEmpty())
    assertTrue(validator.validate(PatchInput(enabled = false)).isEmpty())
    assertTrue(validator.validate(PatchInput(tags = listOf("active"))).isEmpty())
    assertTrue(validator.validate(PatchInput(metadata = mapOf("key" to "value"))).isEmpty())
    assertTrue(validator.validate(PatchInput(items = arrayOf("active"))).isEmpty())
    assertTrue(validator.validate(BooleanPatchInput(isActive = false)).isEmpty())
  }

  // --------------------------------------------------

  @Test
  fun rejectsUnknownNamedProperty() {
    val exception = assertThrows(ValidationException::class.java) {
      validator.validate(InvalidPatchInput())
    }

    assertTrue(exception.cause?.message.orEmpty().contains("Unknown property"))
  }

}

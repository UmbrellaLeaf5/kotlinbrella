package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NeutralConstraintsTest {

  private val validator = Validation.buildDefaultValidatorFactory().validator

  // MARK: Accept library defaults without custom messages
  // --------------------------------------------------

  @Test
  fun acceptsValidNeutralValues() {
    assertTrue(validator.validate(EmailInput("john.doe@example.com")).isEmpty())
    assertTrue(validator.validate(UuidInput("123e4567-e89b-12d3-a456-426614174000")).isEmpty())
    assertTrue(validator.validate(EnumInput("PRINT")).isEmpty())
    assertTrue(validator.validate(RequiredFieldInput("name")).isEmpty())
    assertTrue(validator.validate(PositiveNumberInput("3", "2.5")).isEmpty())
  }

  // --------------------------------------------------

  @Test
  fun rejectsInvalidNeutralValues() {
    assertEquals(1, validator.validate(EmailInput("invalid-email")).size)
    assertEquals(1, validator.validate(UuidInput("not-a-uuid")).size)
    assertEquals(1, validator.validate(EnumInput("INVALID")).size)
    assertEquals(1, validator.validate(RequiredFieldInput("")).size)
    assertEquals(2, validator.validate(RequiredFieldInput(null)).size)
    assertEquals(1, validator.validate(PositiveNumberInput("0", "2.5")).size)
    assertEquals(1, validator.validate(PositiveNumberInput("3", "-1.5")).size)
    assertEquals(1, validator.validate(PositiveNumberInput("3.5", "2.5")).size)
  }

}

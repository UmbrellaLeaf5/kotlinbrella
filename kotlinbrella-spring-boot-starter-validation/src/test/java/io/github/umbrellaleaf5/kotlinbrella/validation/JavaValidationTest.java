package io.github.umbrellaleaf5.kotlinbrella.validation;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaValidationTest {

  @Test
  void supportsJavaRecordsAndBeans() {
    var validator = Validation.buildDefaultValidatorFactory().getValidator();

    assertEquals(1, validator.validate(new PatchRecord(null, null)).size());
    assertTrue(validator.validate(new PatchRecord(null, 0)).isEmpty());
    assertEquals(1, validator.validate(new PatchBean("  ")).size());
    assertTrue(validator.validate(new PatchBean("Ada")).isEmpty());
  }
}

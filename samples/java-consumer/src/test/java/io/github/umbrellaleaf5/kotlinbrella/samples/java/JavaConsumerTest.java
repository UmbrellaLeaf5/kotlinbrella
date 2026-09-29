package io.github.umbrellaleaf5.kotlinbrella.samples.java;

import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision;
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException;
import io.github.umbrellaleaf5.kotlinbrella.util.Conversions;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaConsumerTest {

  @Test
  void usesKotlinbrellaPublicApisFromJava() {
    var id = UUID.randomUUID();

    assertEquals(id, Conversions.uuid(id.toString()));
    assertEquals(404, NotFoundException.unified("Missing").getStatus());
    assertEquals(AccessDecision.ALLOWED, new JavaChecker().check("item", "user"));
    assertEquals(1, Validation.buildDefaultValidatorFactory().getValidator()
      .validate(new JavaPatchInput(null)).size());
    assertTrue(Validation.buildDefaultValidatorFactory().getValidator()
      .validate(new JavaPatchInput("valid")).isEmpty());
  }
}

package io.github.umbrellaleaf5.kotlinbrella.samples.java;

import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision;
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException;
import io.github.umbrellaleaf5.kotlinbrella.util.Conversions;
import io.github.umbrellaleaf5.kotlinbrella.util.NullChecksFacade;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Test
  void usesNullChecksAndNullableConversionsFromJava() {
    var id = UUID.randomUUID();

    assertEquals("value", NullChecksFacade.requireNotNullByName("value", "name"));

    var failure = assertThrows(IllegalArgumentException.class,
      () -> NullChecksFacade.requireFieldNotNullByNameInObject(null, String.class,
        "name", "User", id));

    assertEquals("String.name cannot be null in User: " + id, failure.getMessage());
    assertEquals(id, Conversions.uuidOrNull(id.toString()));
    assertNull(Conversions.uuidOrNull("not-a-uuid"));
    assertNull(Conversions.integerOrNull(null));
  }
}

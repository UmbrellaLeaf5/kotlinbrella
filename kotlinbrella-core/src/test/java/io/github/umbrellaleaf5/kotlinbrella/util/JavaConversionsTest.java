package io.github.umbrellaleaf5.kotlinbrella.util;

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavaConversionsTest {

  @Test
  void parsesValuesFromJavaWithoutExtensionSyntax() {
    assertEquals(42, Conversions.integer("42"));
    assertEquals(Thread.State.NEW, Conversions.enumValue("NEW", Thread.State.class));
    assertThrows(BadRequestException.class, () -> Conversions.enumValue("new", Thread.State.class));
  }
}

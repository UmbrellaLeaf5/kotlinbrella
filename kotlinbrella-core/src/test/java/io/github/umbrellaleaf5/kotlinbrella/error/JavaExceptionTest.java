package io.github.umbrellaleaf5.kotlinbrella.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaExceptionTest {

  @Test
  void factoriesAndAccessorsAreAvailableToJava() {
    NotFoundException exception = NotFoundException.unified("missing");
    assertEquals(404, exception.getStatus());
    assertEquals("missing", exception.getPublicDetail());
  }
}

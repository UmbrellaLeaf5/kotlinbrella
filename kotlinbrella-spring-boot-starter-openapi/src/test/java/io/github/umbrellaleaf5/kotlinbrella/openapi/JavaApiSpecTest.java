package io.github.umbrellaleaf5.kotlinbrella.openapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaApiSpecTest {

  @ApiErrors({@ApiError(code = "NOT_FOUND", detail = "Missing")})
  public void endpoint() {}

  @Test
  void annotationsCanBeDeclaredFromJava() throws NoSuchMethodException {
    var annotation = getClass().getMethod("endpoint").getAnnotation(ApiErrors.class);
    assertEquals("NOT_FOUND", annotation.value()[0].code());
  }
}

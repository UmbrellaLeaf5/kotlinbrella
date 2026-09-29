package io.github.umbrellaleaf5.kotlinbrella.access;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaAccessCheckerTest implements AccessChecker {

  @Override
  public String getResource() {
    return "listing";
  }

  @Override
  public AccessDecision check(String resourceIdString, String userIdString) {
    return AccessDecision.ALLOWED;
  }

  @Test
  void checkersCanBeImplementedFromJava() {
    assertEquals(AccessDecision.ALLOWED, check("item", "user"));
  }
}

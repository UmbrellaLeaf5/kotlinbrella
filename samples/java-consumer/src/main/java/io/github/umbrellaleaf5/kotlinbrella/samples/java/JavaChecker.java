package io.github.umbrellaleaf5.kotlinbrella.samples.java;

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker;
import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision;

public class JavaChecker implements AccessChecker {

  @Override
  public String getResource() {
    return "item";
  }

  @Override
  public AccessDecision check(String resourceIdString, String userIdString) {
    return AccessDecision.ALLOWED;
  }
}

package io.github.umbrellaleaf5.kotlinbrella.validation;

@AtLeastOnePresent(properties = {"name"})
public class PatchBean {

  private final String name;

  public PatchBean(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

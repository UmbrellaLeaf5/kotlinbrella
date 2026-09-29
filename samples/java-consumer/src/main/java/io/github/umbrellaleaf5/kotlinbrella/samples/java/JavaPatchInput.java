package io.github.umbrellaleaf5.kotlinbrella.samples.java;

import io.github.umbrellaleaf5.kotlinbrella.validation.AtLeastOnePresent;

@AtLeastOnePresent(properties = {"name"})
public record JavaPatchInput(String name) {}

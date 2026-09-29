package io.github.umbrellaleaf5.kotlinbrella.validation;

@AtLeastOnePresent(properties = {"name", "count"})
public record PatchRecord(String name, Integer count) {}

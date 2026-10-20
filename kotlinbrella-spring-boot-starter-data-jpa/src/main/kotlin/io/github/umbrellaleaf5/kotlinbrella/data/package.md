# Package io.github.umbrellaleaf5.kotlinbrella.data

Repository helpers for Spring Data.

`findByIdOrThrow` composes a `NotFoundException` with the entity name and
identifier, so every service reports a missing resource the same way. See the
[Spring Data guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#spring-data-jpa).

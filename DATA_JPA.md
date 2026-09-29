# Spring Data JPA Starter

Add `kotlinbrella-spring-boot-starter-data-jpa` and import
`io.github.umbrellaleaf5.kotlinbrella.data.findByIdOrThrow` to use
`CrudRepository<T, ID>.findByIdOrThrow(id, entityName)` with arbitrary ID types.
Missing rows produce a stable `NOT_FOUND` code. Production details contain
only the entity name; the identifier is reserved for diagnostics.

When the Web MVC starter is also installed, optimistic-lock and integrity
violations become safe `409` problems. Constraint names and raw SQL are never
included in production responses. Invalid sort/property names should be
validated at the web boundary, where an application can distinguish an
untrusted client field from an erroneous internal query. This module leaves
them as internal failures rather than guessing which case occurred.

Applications with an existing error JSON contract can set
`kotlinbrella.data-jpa.errors.enabled=false` until their persistence error
responses are migrated. Repository lookup helpers remain available when
the advice is disabled.

The focused JPA starter includes Spring Data JPA and Kotlin reflection.
The aggregate starter additionally includes Liquibase and the PostgreSQL
runtime driver for the Digital Factory full-stack case. PostgreSQL integration
tests use Testcontainers; `./gradlew build` runs them when Docker is available.

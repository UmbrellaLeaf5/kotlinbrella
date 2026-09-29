# Samples

Run `./gradlew build` to compile all three consumer samples and their tests.

- `samples/full-mvc/` is a runnable Kotlin Boot API using the aggregate
  starter. It demonstrates problem responses, conversions, partial-input
  validation, generic repository lookup, `@ApiErrors`, `@CheckOwnership`,
  and an efficient application-provided checker. Its Testcontainers test
  verifies PostgreSQL, ownership concealment and generated OpenAPI.
- `samples/minimal-mvc/` uses only the Web MVC starter. Its context test
  verifies JPA, Springdoc and AspectJ are absent from its classpath.
- `samples/java-consumer/` compiles Java uses of converters, exceptions,
  checker interfaces and Bean Validation.

For interactive use of the full sample, provide `SAMPLE_DATABASE_URL`,
`SAMPLE_DATABASE_USER`, and `SAMPLE_DATABASE_PASSWORD` for a PostgreSQL
instance and run `./gradlew :samples:full-mvc:bootRun`. The Liquibase
changelog creates the sample table. No local infrastructure values are
embedded in the sample configuration.

# Kotlinbrella

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple?logo=kotlin)](https://kotlinlang.org)
[![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-3.5%2B-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8%2B-02303A?logo=gradle)](https://gradle.org)
[![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)](https://junit.org)

<img align="right" height="256" src="icon.png"/>

**Kotlinbrella** is an opinionated Kotlin-first Spring Boot starter suite for
consistent backend APIs. It extracts and evolves production patterns for error
handling, validation, Spring Data, access checks, and OpenAPI documentation.

- **[Guide](GUIDE.md)** — use cases, configuration, recipes, migration notes.
- **[API reference](https://umbrellaleaf5.github.io/kotlinbrella/)** — Dokka
  documentation published for every release.
- **[TODO](TODO.md)** — remaining migration and release work.
- **[Contributing](CONTRIBUTING.md)** — build and review conventions.

## Modules

| Module                                         | Purpose                                  |
| ---------------------------------------------- | ---------------------------------------- |
| `kotlinbrella-core`                            | Errors, conversions, public contracts.   |
| `kotlinbrella-spring-boot-autoconfigure`       | Conditional Spring Boot configuration.   |
| `kotlinbrella-spring-boot-starter-webmvc`      | MVC problem responses and request IDs.   |
| `kotlinbrella-spring-boot-starter-validation`  | Explicit patch validation.               |
| `kotlinbrella-spring-boot-starter-data-jpa`    | Repository lookups and safe conflicts.   |
| `kotlinbrella-spring-boot-starter-openapi`     | Springdoc errors and schemas.            |
| `kotlinbrella-spring-boot-starter-access`      | Ownership checks via application policy. |
| `kotlinbrella-spring-boot-starter`             | Complete aggregate starter.              |
| `kotlinbrella-test`                            | Error-docs parity contract for tests.    |

## Principles

- Kotlin-first implementation with practical Java interoperability.
- One full dependency for opinionated services, focused starters for minimal services.
- One informative client message per error; no profile-dependent redaction.
- Runtime client failures are always the `ApiException` family; see
  [GUIDE.md](GUIDE.md) for the policy.
- Correct HTTP semantics for validation, missing resources, method/media errors,
  conflicts, and unexpected failures.
- Springdoc documentation generated from the same error contract used at runtime.
- Access checks are declarative, configurable, and independent from application
  entities and repositories.
- Public contracts are versioned; project-specific domain code stays outside the
  library.

## Consuming

Java 21 is required. Releases are consumed from [JitPack](https://jitpack.io)
with no credentials: add `maven { url = uri("https://jitpack.io") }` to the
consumer repositories and depend on group
`com.github.UmbrellaLeaf5.kotlinbrella` with the git tag as version. For local
development, run `./gradlew publishToMavenLocal` and add `mavenLocal()`.

```kotlin
dependencies {
  implementation("com.github.UmbrellaLeaf5.kotlinbrella:kotlinbrella-spring-boot-starter:<tag>")
}
```

The aggregate dependency includes Spring MVC, validation, JPA, Liquibase,
PostgreSQL, Springdoc, AOP, Actuator, RestClient, Jackson Kotlin and Kotlin
reflection. Domain-specific integrations remain application dependencies.

## Development

The repository uses Gradle, Kotlin, JUnit 5, and the conventions in
[AGENTS.md](AGENTS.md) and [CODE-STYLE.md](CODE-STYLE.md).

```bash
./gradlew build
```

The full sample requires PostgreSQL settings `SAMPLE_DATABASE_URL`,
`SAMPLE_DATABASE_USER`, and `SAMPLE_DATABASE_PASSWORD`. `./gradlew build`
runs its Testcontainers-backed contract tests when Docker is available. The
minimal sample has no JPA, AOP or Springdoc dependency.

## License

[Unlicense](LICENSE) - public domain.

<a href="https://www.flaticon.com/free-icons/times-square" title="times square icons">Times square icons created by Dave Gandy - Flaticon</a>

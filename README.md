# Kotlinbrella

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple?logo=kotlin)](https://kotlinlang.org)
[![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-3.5%2B-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8%2B-02303A?logo=gradle)](https://gradle.org)
[![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5)](https://junit.org)

<img align="right" height="256" src="icon.png"/>

**Kotlinbrella** is an opinionated Kotlin-first Spring Boot starter suite for
consistent backend APIs. It extracts and evolves production patterns for error
handling, validation, Spring Data, access checks, and OpenAPI documentation.

## Planned Modules

| Module                                      | Purpose                                                                                  |
| ------------------------------------------- | ---------------------------------------------------------------------------------------- |
| `kotlinbrella-core`                         | Kotlin utilities, conversions, named null checks, and client-facing exceptions.          |
| `kotlinbrella-spring-boot-starter-webmvc`   | RFC 9457-compatible errors, validation responses, safe diagnostics, and request context. |
| `kotlinbrella-spring-boot-starter-data-jpa` | `findByIdOrThrow`, persistence conflict handling, and optimistic-lock integration.       |
| `kotlinbrella-spring-boot-starter-openapi`  | Springdoc schemas, error examples, and declarative endpoint error documentation.         |
| `kotlinbrella-spring-boot-starter-access`   | Configurable `@CheckOwnership` backed by application-provided access checkers.           |
| `kotlinbrella-spring-boot-starter`          | Aggregate starter for the complete Kotlinbrella experience.                              |

## Principles

- Kotlin-first implementation with practical Java interoperability.
- One full dependency for opinionated services, focused starters for minimal services.
- Immutable exceptions with safe public details and optional diagnostics.
- Correct HTTP semantics for validation, missing resources, method/media errors,
  conflicts, and unexpected failures.
- Springdoc documentation generated from the same error contract used at runtime.
- Access checks are declarative, configurable, and independent from application
  entities and repositories.
- Public contracts are versioned; project-specific domain code stays outside the
  library.

## Status

Kotlinbrella is in the design and foundation stage. The detailed architecture,
implementation order, testing strategy, migration path, and release checklist
are maintained in [TODO.md](TODO.md).

## Development

The repository uses Gradle, Kotlin, JUnit 5, and the conventions in
[AGENTS.md](AGENTS.md) and [CODE-STYLE.md](CODE-STYLE.md).

```bash
./gradlew build
```

Java 21 is required. The Spring Boot baseline is 4.0.3. For local development
of consumers, run `./gradlew publishToMavenLocal` and add `mavenLocal()` to the
consumer repositories. Published module coordinates use group
`io.github.umbrellaleaf5.kotlinbrella` and version `0.1.0-SNAPSHOT`.

Publication to Maven Central and signing require release credentials and are
tracked in [TODO.md](TODO.md).

## License

[Unlicense](LICENSE) - public domain.

<a href="https://www.flaticon.com/free-icons/times-square" title="times square icons">Times square icons created by Dave Gandy - Flaticon</a>

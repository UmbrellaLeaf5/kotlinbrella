# Kotlinbrella Implementation Plan

This document is the implementation backlog and architecture record for
Kotlinbrella. Complete phases in order unless a later item explicitly depends
on a stable earlier public contract. Do not publish an artifact whose public
API lacks unit and integration coverage.

## Product Constraints

- [x] Keep Kotlinbrella Kotlin-first and useful to Digital Factory and Terraaero.
- [x] Make public APIs practical for Java consumers where that does not weaken
      Kotlin ergonomics.
- [x] Ship an aggregate starter for the usual full-stack case and focused
      starters for services that need only a subset.
- [x] Never copy project entities, repositories, error text, S3 workflows,
      queues, or deployment configuration into Kotlinbrella.
- [ ] Treat public types, annotations, properties, error codes, JSON fields,
      and documentation as versioned compatibility contracts.
- [ ] Library policy: runtime client failures are the `ApiException` family
      only; configuration failures are `IllegalStateException` or
      `IllegalArgumentException`. Document the policy in `ERROR_CONTRACT.md`
      and `README.md`.

## Target Modules

- [x] `kotlinbrella-core`: framework-free Kotlin utilities and exceptions.
- [x] `kotlinbrella-spring-boot-autoconfigure`: conditional configurations.
- [x] `kotlinbrella-spring-boot-starter-webmvc`: MVC errors and request context.
- [x] `kotlinbrella-spring-boot-starter-validation`: neutral patch validation.
- [x] `kotlinbrella-spring-boot-starter-data-jpa`: repository helpers and JPA errors.
- [x] `kotlinbrella-spring-boot-starter-openapi`: Springdoc integration.
- [x] `kotlinbrella-spring-boot-starter-access`: ownership/access AOP adapter.
- [x] `kotlinbrella-spring-boot-starter`: aggregate of stable focused starters.
- [x] `samples/`: full Kotlin MVC, minimal MVC, and Java consumer examples.

## Phase 0: Repository Foundation

- [x] Create Gradle wrapper, `settings.gradle.kts`, root `build.gradle.kts`,
      and every target module.
- [x] Declare plugins with `apply false`; centralize dependency versions in one
      chosen mechanism and use it consistently.
- [x] Target Java 17 unless the supported Boot matrix establishes another
      baseline; configure Kotlin JVM and strict JSR-305 handling.
- [x] Configure JUnit 5, reproducible archives, sources, Dokka/Javadoc, and
      Gradle publishing metadata.
- [x] Add CI for clean build, tests, samples, and the supported JDK/Boot matrix.
- [ ] Decide semantic versioning, changelog, license, security policy, Maven
      Central publication, signing, and CI secret handling.

Acceptance:

- [x] `./gradlew build` works from a clean checkout.
- [x] Every module has an intentional artifact name and POM description.

## Phase 1: Canonical Error Contract

- [x] Choose one RFC 9457-compatible external error model before writing an
      exception handler.
- [x] Document stable fields: `type`, `title`, `status`, `detail`, `instance`,
      `code`, `traceId`, and `violations`.
- [x] Define `ErrorViolation(field, message, code)` and an uppercase stable
      error-code namespace.
- [x] Decide the problem-type URI strategy and message localization policy.
- [x] Implement immutable `ApiException` with status, code, public detail,
      diagnostic detail, and cause preservation.
- [x] Implement or factory-create `BadRequestException`, `NotFoundException`,
      `ConflictException`, and `ForbiddenException`.
- [ ] Add typical 5xx types (`InternalException`, `PayloadTooLargeException`,
      `ServiceUnavailableException`) reusing the existing constructor and
      `unified(...)` shape, with unit coverage.
- [x] Resolve every documented error code to its HTTP status (`ErrorCode.status`)
      and reason phrase (`ErrorCode.title`, exposed as `ApiError.status/title`).
- [x] Preserve the Terraaero `unified(...)` convenience for equal details.
- [x] Preserve separate diagnostic and public messages without mutable static
      state or hard-coded assumptions that development is named `dev`.
- [x] Test exception status, code, messages, cause, immutability, and Java use.

Acceptance:

- [x] `kotlinbrella-core` has no Spring, JPA, Springdoc, servlet, WebFlux, or
      AspectJ dependency.
- [x] Error code and message behavior are thoroughly unit tested.

## Phase 2: Core Kotlin Utilities

- [x] Port Terraaero `requireNotNullByName`, `requireFieldNotNullByName`,
      `checkNotNullByName`, and `checkFieldNotNullByName`.
- [x] Keep semantics strict: `require...` means invalid caller input and
      `IllegalArgumentException`; `check...` means an internal invariant and
      `IllegalStateException`.
- [ ] Generalize context helpers so they do not require UUID identifiers.
- [x] Keep lazy names/messages for successful paths.
- [x] Port conversion helpers for UUID, integer, long, double, instant, and enum.
- [ ] Add `*OrNull` variants (`toUUIDOrNull`, `toIntOrNull`, `toLongOrNull`,
      `toDoubleOrNull`, `toInstantOrNull`, `toEnumOrNull`) returning null for
      malformed input instead of throwing, with unit coverage.
- [x] Make malformed external input a Kotlinbrella bad request with stable codes.
- [x] Define whitespace, overflow, timezone, enum case, and nullability behavior.
- [x] Test every valid, malformed, overflow, and diagnostics path.
- [ ] Add Java-friendly facades only where extensions are impractical in Java.

Acceptance:

- [ ] A sample can eliminate `!!` and raw null-check boilerplate.
- [ ] Client input cannot accidentally become an internal-state failure.

## Phase 3: Web MVC Starter

### Properties and activation

- [x] Define `KotlinbrellaWebProperties` under a stable prefix.
- [x] Define and document an `enabled` switch.
- [x] Add explicit `expose-debug-details`; allow optional configured diagnostic
      profiles via `Environment.acceptsProfiles(...)`, never a first-active-profile
      heuristic.
- [x] Select diagnostics through a single `mode` (`dev`/`prod`, default
      `prod`) set in profile files; all branching lives in the library, never
      in application code. `expose-debug-details`, `diagnostic-profiles`, and
      `log-expected4xx` are removed.
- [x] Add an error-shape switch (`standard` for RFC 9457, `simple` for
      `{error, message}`) so applications on a compact contract can use the
      library handler without changing their response shape.
- [ ] Derive log verbosity from the diagnostics decision: diagnostics on
      (dev) logs full records with details, diagnostics off (prod) logs
      minimal one-line records. Unknown 5xx failures always log full with a
      stack trace regardless of verbosity; rejected values are never logged.
- [x] Filter advice-emitted logs through standard `logging.level` instead of
      a custom threshold: expected 4xx go to info, unexpected 5xx to error,
      the framework drops the rest (no custom setting).
- [x] Define request/trace ID header, response propagation, MDC behavior,
      generation policy, and privacy constraints.
- [ ] Document every new property in `WEB_MVC.md` with defaults.

### Handler

- [x] Auto-configure `@RestControllerAdvice` only for Servlet MVC.
- [x] Give Kotlinbrella advice a deliberate order so applications can override
      individual mappings with higher-precedence advice.
- [x] Render Kotlinbrella exceptions through the canonical contract.
- [x] Map `MethodArgumentNotValidException` to structured field/global errors.
- [x] Map `ConstraintViolationException` for method and request validation.
- [x] Map malformed JSON, invalid enum/date/format, and type mismatch to `400`
      without exposing rejected values by default.
- [x] Map missing request values to `400`, missing resources to `404`, unsupported
      methods to `405`, and unsupported media types to `415`.
- [ ] Map invalid sort/property references (`PropertyReferenceException`) to
      `400` with the offending property name but no query exposure.
- [x] Render the `simple` shape (`error` = reason phrase, `message` = detail,
      violations joined into the message) from the same data as the standard
      shape; register no problem schemas in `simple` mode.
- [x] Route every advice-emitted log through standard levels with the
      derived verbosity; log unknown 5xx failures exactly once, always with
      a stack trace regardless of verbosity.
- [x] Map unknown failures to a safe `500`, log the full exception exactly once,
      and return only safe details plus trace ID.
- [x] Define configurable logging of expected 4xx failures without request-body
      or secret leakage.
- [x] Add Spring context tests for every mapping, response JSON, detail exposure,
      trace ID, advice precedence, and 5xx logging.
- [x] Add context tests for the `simple` shape, standard level filtering,
      both verbosity modes, and the `mode` switch.
- [ ] Keep vendor-specific failures (S3, queues, brokers) out of the library;
      domain exceptions must extend the `ApiException` family to be handled.

Acceptance:

- [x] Combine Terraaero immutable messages and unknown-error logging with DF
      handler coverage and Slicer API's correct `405`/`415` semantics.
- [x] Production responses never reveal DB text, stack traces, or rejected values.

## Phase 4: Validation Starter

- [x] Replace existing reflection-over-all-fields annotations with
      `@AtLeastOnePresent(properties = [...])`.
- [x] Support Kotlin data classes, Java records, and JavaBeans without requiring
      `kotlin-reflect` for consumers that do not otherwise use it.
- [x] Define presence for null, blank text, empty collections/maps, zero, and false.
- [x] Fail clearly if an annotation names an unknown property.
- [x] Emit a useful class-level violation with a stable code.
- [x] Extract only neutral constraints; prefer standard Hibernate Validator
      `@UUID`, `@Email`, `@Positive`, and `@Pattern` over redundant annotations.
- [x] Add allowed-values/enum validation only after a real unmet use case remains.
- [x] Test Kotlin and Java inputs, empty patches, technical non-null fields, and
      message interpolation.

Acceptance:

- [x] Empty patch requests fail; unrelated required fields cannot make them pass.
- [x] No geography, file-upload, or product-domain rules enter this module.

## Phase 5: Spring Data JPA Starter

- [x] Implement generic `CrudRepository<T, ID>.findByIdOrThrow(id, entityName)`.
- [x] Produce Kotlinbrella `NotFoundException` and a stable not-found code.
- [x] Add a factory overload only if specific codes/messages require it.
- [x] Support non-UUID identifier types.
- [x] Map optimistic locking failures to `409 Conflict`.
- [x] Map `DataIntegrityViolationException` conservatively; never expose constraint
      names by default.
- [x] Decide integrity semantics explicitly: the library maps every integrity
      violation to `409` for uniformity; the Digital Factory legacy `400`
      branch for non-unique violations is intentionally not ported. Invalid
      sort/property references are data concerns handled by the JPA advice.
- [x] Decide whether invalid sort/property exceptions are data or web concerns.
- [x] Use Testcontainers or an equivalent real database integration test for
      lookup, uniqueness, foreign keys, and optimistic locks.

Acceptance:

- [x] The module depends on Spring Data/JPA, never application entities.
- [x] S3, upload, queue, and vendor-specific application behavior are excluded.

## Phase 6: OpenAPI Starter

- [x] Add Springdoc as an intentional dependency of the OpenAPI starter.
- [x] Keep the Web MVC starter usable without Springdoc.
- [x] Port DF's `@ApiError` / `@ApiErrors` concept using canonical codes and
      Kotlinbrella's error contract.
- [x] Choose annotation parameter types compatible with Kotlin and Java.
- [x] Implement an `OperationCustomizer` that preserves application-declared
      responses while adding schemas and examples.
- [x] Handle duplicate statuses/codes and multiple examples safely.
- [x] Register canonical error and violation schemas once.
- [x] Test generated OpenAPI JSON against the actual MVC response contract.
- [x] Document API-spec interfaces as preferred, never mandatory.

Acceptance:

- [x] Documentation examples cannot drift from the runtime error renderer.

## Phase 7: Access and Ownership Starter

### Contract

- [x] Separate access decisions from persistence mechanics.
- [x] Define `AccessDecision`: at minimum `ALLOWED`, `NOT_FOUND`, `FORBIDDEN`.
- [x] Define application-provided `OwnershipChecker`/`AccessChecker` registry
      keyed by a stable resource key or type.
- [x] Let checkers use efficient domain queries such as `existsByIdAndUserId`;
      never make a generic aspect load entities and traverse associations.
- [x] Decide whether checkers receive raw strings, parsed IDs, or a request object;
      document conversion and failure behavior.

### Annotation and AOP

- [x] Port ergonomic `@CheckOwnership`.
- [x] Support default argument lookup by configurable templates, for example
      `{resource}IdString` and `userIdString`.
- [x] Support explicit annotation overrides for nonstandard parameter names.
- [x] Support policy that turns forbidden access into `404` to conceal existence,
      as well as an explicit `403` policy.
- [x] Validate missing or duplicate checkers and broken argument configuration
      with actionable startup errors where possible.
- [x] Implement with Spring AOP only in the access starter.
- [x] Document and test proxy limitations, self-invocation, Kotlin `open`
      requirements, and annotation placement on interfaces versus implementations.
- [x] Log decisions safely and document TOCTOU limitations for mutations.
- [x] Test allowed, missing, foreign owner, hidden existence, explicit naming,
      convention naming, no checker, and self-invocation.

Acceptance:

- [x] No DF repository or entity is referenced by Kotlinbrella.
- [x] A DF service can preserve existing `404` concealment through a checker.

## Phase 8: Aggregate Starter and Samples

- [x] Make the aggregate starter depend on all stable focused starters.
- [x] Include OpenAPI in the aggregate starter, while preserving focused modules.
- [x] Build a Kotlin sample for errors, conversion, validation, repository lookup,
      `@ApiErrors`, and `@CheckOwnership`.
- [x] Build a minimal MVC sample proving Web MVC does not pull JPA, AOP, or Springdoc.
- [x] Add Java consumer compilation/integration coverage for public APIs.
- [x] Provide safe example YAML for diagnostics, OpenAPI, access conventions, and
      request IDs. Do not include credentials or local infrastructure defaults.

Acceptance:

- [x] One dependency enables the complete intended experience.
- [x] Focused starters do not pull unrelated infrastructure.

## Phase 9: Full Digital Factory `root` Migration

- [x] Adopt neutral validation (`AtLeastOnePresent`, `ValidUUID`, `ValidEmail`,
      `RequiredField`, `ValidEnum`); reject empty-only patches consistently.
- [x] Migrate OpenAPI declarations to library `ApiError`/`ApiErrors` and compare
      generated specs with the runtime error contract.
- [x] Replace `root`'s `OwnershipAspect` with application-provided checkers
      after integration tests preserve missing-user, `404`, and `403` behavior.
- [x] Migrate null checks to the library; delete the local utility file.
- [x] Migrate string converters (`toUUIDOrThrow`, `toLongOrThrow`,
      `toEnumOrThrow`) to the library; delete `StringExtensions.kt` including
      its unused `toInt`/`toDouble`/`*OrNull` members.
- [x] Migrate generic `findByIdOrThrow` to the library; no Python assertion
      needed changes (checker paths keep their exact messages).
- [x] Migrate all client exceptions to the `ApiException` family with stable
      codes, preserving texts asserted by Python tests (`"active orders"` stays
      a `CONFLICT` detail).
- [x] Rehome the four S3 exceptions onto library exception types with fixed
      codes (`FORBIDDEN`, `NOT_FOUND`, `PAYLOAD_TOO_LARGE`, `INTERNAL_ERROR`).
- [x] Delete `root`'s `GlobalExceptionHandler`, `ProfileUtils`, and
      `ExceptionModeInitializer`; the library advice renders the `legacy`
      shape from `kotlinbrella.web` configuration (shape and mode; levels go
      through standard `logging.level`). No successor handler in
      application code.
- [x] Configure `root` through `kotlinbrella.web` properties only; keep
      `data-jpa.errors.enabled=false` and the `KotlinbrellaOpenApiAutoConfiguration`
      exclusion until their migrations are approved.
- [x] Compare old/new error JSON for every endpoint family before and after;
      the shipped `error`/`message` shape stays byte-compatible, only vetted
      message texts change.
- [x] Run the `root` Gradle build and the full Digital Factory Python autotest
      suite (all 162 tests, not a subset): error paths are covered only there.
- [x] Remove every duplicated annotation, utility class, and customizer after
      its uses have been migrated and the full suite passes.
- [ ] Turn discovered gaps into Kotlinbrella issues, not immediate domain features.

## Phase 10: Slicer API Migration

- [ ] After `root` is fully green, repeat the Phase 9 migration for
      `slicer-api`: null checks, converters, client exceptions, `findByIdOrThrow`,
      neutral validation, ownership (if any), and handler/profile-config removal.
- [ ] Run the `slicer-api` Gradle build and the Python suite paths covering it.

## Phase 11: Future Extensions

- [ ] Separate WebFlux starter with no Servlet types.
- [ ] Optional `Clock` auto-configuration that never replaces an application clock.
- [ ] Kafka conventions: safe deserialization, retry/backoff, DLT, tracing, and
      versioned envelopes, never domain events.
- [ ] Spring Security integration based on authenticated principals, never IDs
      supplied in request bodies.
- [ ] Metrics, tracing, and structured logging extensions.
- [ ] KSP research only after two applications share a stable interface-to-delegate
      contract; do not begin with a reflection code generator.
- [ ] Optional architecture-test toolkit for project conventions, distinct from
      runtime starter behavior.

## Release Checklist

- [ ] Review public API compatibility and document migration.
- [ ] Confirm every runtime client failure uses the `ApiException` family.
- [ ] Run build, integration tests, compatibility matrix, and samples.
- [ ] Verify generated OpenAPI matches MVC responses.
- [ ] Verify dependency graph: core stays framework-free and focused starters
      have no accidental dependencies.
- [ ] Scan sources, tests, docs, and build outputs for credentials, tokens, local
      paths, and development URLs.
- [ ] Generate release notes with supported versions and configuration changes.
- [ ] Publish signed artifacts, sources, documentation, and checksums.

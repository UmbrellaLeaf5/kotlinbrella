# Kotlinbrella Implementation Plan

This document is the implementation backlog and architecture record for
Kotlinbrella. Complete phases in order unless a later item explicitly depends
on a stable earlier public contract. Do not publish an artifact whose public
API lacks unit and integration coverage.

## Product Constraints

- [ ] Keep Kotlinbrella Kotlin-first and useful to Digital Factory and Terraaero.
- [ ] Make public APIs practical for Java consumers where that does not weaken
      Kotlin ergonomics.
- [ ] Ship an aggregate starter for the usual full-stack case and focused
      starters for services that need only a subset.
- [ ] Never copy project entities, repositories, error text, S3 workflows,
      queues, or deployment configuration into Kotlinbrella.
- [ ] Treat public types, annotations, properties, error codes, JSON fields,
      and documentation as versioned compatibility contracts.

## Target Modules

- [ ] `kotlinbrella-core`: framework-free Kotlin utilities and exceptions.
- [ ] `kotlinbrella-spring-boot-autoconfigure`: conditional configurations.
- [ ] `kotlinbrella-spring-boot-starter-webmvc`: MVC errors and request context.
- [ ] `kotlinbrella-spring-boot-starter-data-jpa`: repository helpers and JPA errors.
- [ ] `kotlinbrella-spring-boot-starter-openapi`: Springdoc integration.
- [ ] `kotlinbrella-spring-boot-starter-access`: ownership/access AOP adapter.
- [ ] `kotlinbrella-spring-boot-starter`: aggregate of stable focused starters.
- [ ] `samples/`: full Kotlin MVC, minimal MVC, and Java consumer examples.

## Phase 0: Repository Foundation

- [x] Create Gradle wrapper, `settings.gradle.kts`, root `build.gradle.kts`,
      and every target module.
- [x] Declare plugins with `apply false`; centralize dependency versions in one
      chosen mechanism and use it consistently.
- [x] Target Java 17 unless the supported Boot matrix establishes another
      baseline; configure Kotlin JVM and strict JSR-305 handling.
- [x] Configure JUnit 5, reproducible archives, sources, Dokka/Javadoc, and
      Gradle publishing metadata.
- [ ] Add CI for clean build, tests, samples, and the supported JDK/Boot matrix.
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
- [x] Preserve the Terraaero `unified(...)` convenience for equal details.
- [x] Preserve separate diagnostic and public messages without mutable static
      state or hard-coded assumptions that development is named `dev`.
- [x] Test exception status, code, messages, cause, immutability, and Java use.

Acceptance:

- [x] `kotlinbrella-core` has no Spring, JPA, Springdoc, servlet, WebFlux, or
      AspectJ dependency.
- [x] Error code and message behavior are thoroughly unit tested.

## Phase 2: Core Kotlin Utilities

- [ ] Port Terraaero `requireNotNullByName`, `requireFieldNotNullByName`,
      `checkNotNullByName`, and `checkFieldNotNullByName`.
- [ ] Keep semantics strict: `require...` means invalid caller input and
      `IllegalArgumentException`; `check...` means an internal invariant and
      `IllegalStateException`.
- [ ] Generalize context helpers so they do not require UUID identifiers.
- [ ] Keep lazy names/messages for successful paths.
- [ ] Port conversion helpers for UUID, integer, long, double, instant, and enum.
- [ ] Make malformed external input a Kotlinbrella bad request with stable codes.
- [ ] Define whitespace, overflow, timezone, enum case, and nullability behavior.
- [ ] Test every valid, malformed, overflow, and diagnostics path.
- [ ] Add Java-friendly facades only where extensions are impractical in Java.

Acceptance:

- [ ] A sample can eliminate `!!` and raw null-check boilerplate.
- [ ] Client input cannot accidentally become an internal-state failure.

## Phase 3: Web MVC Starter

### Properties and activation

- [ ] Define `KotlinbrellaWebProperties` under a stable prefix.
- [ ] Define and document an `enabled` switch.
- [ ] Add explicit `expose-debug-details`; allow optional configured diagnostic
      profiles via `Environment.acceptsProfiles(...)`, never a first-active-profile
      heuristic.
- [ ] Define request/trace ID header, response propagation, MDC behavior,
      generation policy, and privacy constraints.

### Handler

- [ ] Auto-configure `@RestControllerAdvice` only for Servlet MVC.
- [ ] Give Kotlinbrella advice a deliberate order so applications can override
      individual mappings with higher-precedence advice.
- [ ] Render Kotlinbrella exceptions through the canonical contract.
- [ ] Map `MethodArgumentNotValidException` to structured field/global errors.
- [ ] Map `ConstraintViolationException` for method and request validation.
- [ ] Map malformed JSON, invalid enum/date/format, and type mismatch to `400`
      without exposing rejected values by default.
- [ ] Map missing request values to `400`, missing resources to `404`, unsupported
      methods to `405`, and unsupported media types to `415`.
- [ ] Map unknown failures to a safe `500`, log the full exception exactly once,
      and return only safe details plus trace ID.
- [ ] Define configurable logging of expected 4xx failures without request-body
      or secret leakage.
- [ ] Add Spring context tests for every mapping, response JSON, detail exposure,
      trace ID, advice precedence, and 5xx logging.

Acceptance:

- [ ] Combine Terraaero immutable messages and unknown-error logging with DF
      handler coverage and Slicer API's correct `405`/`415` semantics.
- [ ] Production responses never reveal DB text, stack traces, or rejected values.

## Phase 4: Validation Starter

- [ ] Replace existing reflection-over-all-fields annotations with
      `@AtLeastOnePresent(properties = [...])`.
- [ ] Support Kotlin data classes, Java records, and JavaBeans without requiring
      `kotlin-reflect` for consumers that do not otherwise use it.
- [ ] Define presence for null, blank text, empty collections/maps, zero, and false.
- [ ] Fail clearly if an annotation names an unknown property.
- [ ] Emit a useful class-level violation with a stable code.
- [ ] Extract only neutral constraints; prefer standard Hibernate Validator
      `@UUID`, `@Email`, `@Positive`, and `@Pattern` over redundant annotations.
- [ ] Add allowed-values/enum validation only after a real unmet use case remains.
- [ ] Test Kotlin and Java inputs, empty patches, technical non-null fields, and
      message interpolation.

Acceptance:

- [ ] Empty patch requests fail; unrelated required fields cannot make them pass.
- [ ] No geography, file-upload, or product-domain rules enter this module.

## Phase 5: Spring Data JPA Starter

- [ ] Implement generic `CrudRepository<T, ID>.findByIdOrThrow(id, entityName)`.
- [ ] Produce Kotlinbrella `NotFoundException` and a stable not-found code.
- [ ] Add a factory overload only if specific codes/messages require it.
- [ ] Support non-UUID identifier types.
- [ ] Map optimistic locking failures to `409 Conflict`.
- [ ] Map `DataIntegrityViolationException` conservatively; never expose constraint
      names by default.
- [ ] Decide whether invalid sort/property exceptions are data or web concerns.
- [ ] Use Testcontainers or an equivalent real database integration test for
      lookup, uniqueness, foreign keys, and optimistic locks.

Acceptance:

- [ ] The module depends on Spring Data/JPA, never application entities.
- [ ] S3, upload, queue, and vendor-specific application behavior are excluded.

## Phase 6: OpenAPI Starter

- [ ] Add Springdoc as an intentional dependency of the OpenAPI starter.
- [ ] Keep the Web MVC starter usable without Springdoc.
- [ ] Port DF's `@ApiError` / `@ApiErrors` concept using canonical codes and
      Kotlinbrella's error contract.
- [ ] Choose annotation parameter types compatible with Kotlin and Java.
- [ ] Implement an `OperationCustomizer` that preserves application-declared
      responses while adding schemas and examples.
- [ ] Handle duplicate statuses/codes and multiple examples safely.
- [ ] Register canonical error and violation schemas once.
- [ ] Test generated OpenAPI JSON against the actual MVC response contract.
- [ ] Document API-spec interfaces as preferred, never mandatory.

Acceptance:

- [ ] Documentation examples cannot drift from the runtime error renderer.

## Phase 7: Access and Ownership Starter

### Contract

- [ ] Separate access decisions from persistence mechanics.
- [ ] Define `AccessDecision`: at minimum `ALLOWED`, `NOT_FOUND`, `FORBIDDEN`.
- [ ] Define application-provided `OwnershipChecker`/`AccessChecker` registry
      keyed by a stable resource key or type.
- [ ] Let checkers use efficient domain queries such as `existsByIdAndUserId`;
      never make a generic aspect load entities and traverse associations.
- [ ] Decide whether checkers receive raw strings, parsed IDs, or a request object;
      document conversion and failure behavior.

### Annotation and AOP

- [ ] Port ergonomic `@CheckOwnership`.
- [ ] Support default argument lookup by configurable templates, for example
      `{resource}IdString` and `userIdString`.
- [ ] Support explicit annotation overrides for nonstandard parameter names.
- [ ] Support policy that turns forbidden access into `404` to conceal existence,
      as well as an explicit `403` policy.
- [ ] Validate missing or duplicate checkers and broken argument configuration
      with actionable startup errors where possible.
- [ ] Implement with Spring AOP only in the access starter.
- [ ] Document and test proxy limitations, self-invocation, Kotlin `open`
      requirements, and annotation placement on interfaces versus implementations.
- [ ] Log decisions safely and document TOCTOU limitations for mutations.
- [ ] Test allowed, missing, foreign owner, hidden existence, explicit naming,
      convention naming, no checker, and self-invocation.

Acceptance:

- [ ] No DF repository or entity is referenced by Kotlinbrella.
- [ ] A DF service can preserve existing `404` concealment through a checker.

## Phase 8: Aggregate Starter and Samples

- [ ] Make the aggregate starter depend on all stable focused starters.
- [ ] Include OpenAPI in the aggregate starter, while preserving focused modules.
- [ ] Build a Kotlin sample for errors, conversion, validation, repository lookup,
      `@ApiErrors`, and `@CheckOwnership`.
- [ ] Build a minimal MVC sample proving Web MVC does not pull JPA, AOP, or Springdoc.
- [ ] Add Java consumer compilation/integration coverage for public APIs.
- [ ] Provide safe example YAML for diagnostics, OpenAPI, access conventions, and
      request IDs. Do not include credentials or local infrastructure defaults.

Acceptance:

- [ ] One dependency enables the complete intended experience.
- [ ] Focused starters do not pull unrelated infrastructure.

## Phase 9: Existing-Service Migration

- [ ] Migrate a low-risk Terraaero slice first: exceptions, converters, and
      generic `findByIdOrThrow`.
- [ ] Compare old/new error JSON; write a compatibility adapter only if a shipped
      external contract requires one.
- [ ] Migrate Terraaero global errors after response-contract tests exist.
- [ ] Migrate DF OpenAPI declarations and compare generated specs.
- [ ] Replace DF `OwnershipAspect` only after checker integration tests preserve
      intended `404`/`403` behavior.
- [ ] Delete duplicated project utility code only after use of a released artifact.
- [ ] Turn discovered gaps into Kotlinbrella issues, not immediate domain features.

## Phase 10: Future Extensions

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
- [ ] Run build, integration tests, compatibility matrix, and samples.
- [ ] Verify generated OpenAPI matches MVC responses.
- [ ] Verify dependency graph: core stays framework-free and focused starters
      have no accidental dependencies.
- [ ] Scan sources, tests, docs, and build outputs for credentials, tokens, local
      paths, and development URLs.
- [ ] Generate release notes with supported versions and configuration changes.
- [ ] Publish signed artifacts, sources, documentation, and checksums.

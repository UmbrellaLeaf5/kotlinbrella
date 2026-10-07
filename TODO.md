# Kotlinbrella Implementation Plan

This document is the implementation backlog and architecture record for
Kotlinbrella. Complete phases in order unless a later item explicitly depends
on a stable earlier public contract. Do not publish an artifact whose public
API lacks unit and integration coverage.

## Product Constraints

- [ ] Treat public types, annotations, properties, error codes, JSON fields,
      and documentation as versioned compatibility contracts.
- [ ] Library policy: runtime client failures are the `ApiException` family
      only; configuration failures are `IllegalStateException` or
      `IllegalArgumentException`. Document the policy in `GUIDE.md` and
      `README.md`.

## Phase 0: Repository Foundation

- [ ] Decide semantic versioning, license, security policy, Maven Central
      publication, signing, and CI secret handling. Release notes live in
      GitHub releases; there is no `CHANGELOG.md` file.

## Phase 2: Core Kotlin Utilities

Acceptance:

- [ ] A sample can eliminate `!!` and raw null-check boilerplate.
- [ ] Client input cannot accidentally become an internal-state failure.

## Phase 3: Web MVC Starter

- [ ] Keep vendor-specific failures (S3, queues, brokers) out of the library;
      domain exceptions must extend the `ApiException` family to be handled.

## Phase 9: Full Digital Factory `root` Migration

- [ ] Decide `root` JPA error mappings. The library advice is currently
      disabled (`kotlinbrella.data-jpa.errors.enabled=false`) while `root`'s
      own `DataAccessException` branch is deleted, so duplicate-key, unique,
      integrity, and sort failures fall through to generic 500. Either enable
      the library advice (integrity always 409, sort 400 with identical text)
      and verify with targeted probes plus the full suite, or record keeping
      it off as an explicit decision.
- [ ] Remove migration leftovers in `root`: unreferenced `Constants.Exception`,
      `Constants.Validation`, `Constants.Debug`, `Constants.Pattern`, and
      `DetailedErrorMessage` subsets, plus unused `SlicerClient` imports.
- [ ] Turn discovered gaps into Kotlinbrella issues, not immediate domain features.

## Phase 10: Slicer API Migration

- [ ] After `root` is fully green, repeat the Phase 9 migration for
      `slicer-api`: null checks, converters, client exceptions, `findByIdOrThrow`,
      handler/profile-config removal.
- [ ] Give `slicer-api` the validation it lacks: neutral annotations on
      `CalculationRequest`, `*String` controller/service boundaries with
      library conversions, and request-id behavior through starter defaults.
- [ ] Document `slicer-api` errors with library annotations in `simple` shape;
      configure `kotlinbrella.web` per profile (mode, shape) and delete its
      handler and profile utilities. Keep domain consts, scheduler, and queue code.
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

## Phase 12: Documentation and API Reference

- [ ] Write public KDoc in English for every public type, function, and property;
      update the `CODE-STYLE.md` language rule accordingly.
- [ ] Merge user docs into a single `GUIDE.md` organized by use cases (errors in
      both shapes, validation, access, JPA, OpenAPI, conversions, samples);
      trim `README.md` to a front page with links.
- [ ] Add the Dokka plugin (version in the root version mechanism), an aggregate
      HTML build, and a `docs.yml` workflow deploying to GitHub Pages (requires
      enabling Pages from Actions in the repository settings).
- [ ] Delete the merged files (`WEB_MVC.md`, `ACCESS.md`, `OPENAPI.md`,
      `DATA_JPA.md`, `VALIDATION.md`, `CORE_UTILITIES.md`, `ERROR_CONTRACT.md`,
      `SAMPLES.md`, `CHANGELOG.md`); fix all links.

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

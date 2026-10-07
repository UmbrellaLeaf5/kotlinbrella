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

- [x] Decide semantic versioning, license, security policy, JitPack publication
      (git tags as versions, no credentials), and CI secret handling. Release
      notes live in GitHub releases; there is no `CHANGELOG.md` file.

## Phase 2: Core Kotlin Utilities

Acceptance:

- [ ] A sample can eliminate `!!` and raw null-check boilerplate.
- [ ] Client input cannot accidentally become an internal-state failure.

## Phase 3: Web MVC Starter

- [ ] Keep vendor-specific failures (S3, queues, brokers) out of the library;
      domain exceptions must extend the `ApiException` family to be handled.

## Phase 9: Full Digital Factory `root` Migration

- [x] Decide `root` JPA error mappings. The library advice is enabled
      (`data-jpa.errors.enabled` override removed): integrity always 409,
      sort 400 with identical text. Verified with a live 400 probe
      (`Invalid sort property`) plus the full 162-test suite.
- [x] Remove migration leftovers in `root`: dead `Constants.Exception`
      (except used `S3_OPERATION_FAILED`), `Constants.Validation`,
      `Constants.Debug`, `Constants.Pattern`, and `DetailedErrorMessage`
      subsets, plus file-operation `ApiSpec` descriptions (176/176 members
      live, texts byte-identical). `SlicerClient` has no unused imports.
- [x] Turn discovered gaps into Kotlinbrella issues, not immediate domain features.

## Phase 10: Slicer API Migration (staged, green suite after each step)

- [ ] Step 1.1 — dependency and `IdResponse`: `jitpack.io` instead of
      `mavenLocal`/`0.1.0-SNAPSHOT` (version in `dependencies.gradle.kts`),
      delete the domain `IdResponse` in favor of the library one; verify with
      `./gradlew build` plus a smoke probe. This also removes the last
      `mavenLocal` usage.
- [ ] Step 1.2 — exceptions and handler: domain `BaseClientException` family
      becomes the library `ApiException` family with 1:1 texts; delete
      `GlobalExceptionHandler`, `ProfileUtils`, `FindByIdOrThrow`,
      `ToNotNullOrThrow`, and the domain `ErrorResponse`; verify with the full
      Python suite (the `{error, message}` contract stays intact).
- [ ] Step 1.3 — validation and boundaries: neutral annotations on
      `CalculationRequest`, `*String` controller/service boundaries with
      library conversions, `error-shape: simple` and `mode` per profile,
      `@ApiErrors` on specs; verify with the full suite plus an `/api-docs`
      diff.
- [ ] Step 1.4 — JPA advice and sign-off: drop `enabled: false`, run the
      slicer paths (creation/deletion/estimation/timing) plus targeted
      400/409 probes; suite 162/162 closes the phase.

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

- [ ] Top up public KDoc in English for every public type, function, and property
      (Step 2.2); the `CODE-STYLE.md` language rule is already updated.
- [ ] Merge user docs into a single `GUIDE.md` organized by use cases (errors in
      both shapes, validation, access, JPA, OpenAPI, conversions, samples);
      trim `README.md` to a front page with links. Follows the Dokka curation below.
- [x] Add the Dokka plugin (version in the root version mechanism), an aggregate
      HTML build, and a `docs.yml` workflow deploying to GitHub Pages.
- [ ] Step 2.1 — Dokka curation: `package.md` per public package, suppress
      `*internal*` packages from navigation, README includes on the aggregate page.
- [ ] Step 2.2 — Dokka finish: footer with the tag version plus GitHub link,
      `suppressObviousFunctions`, KDoc top-up (see above).
- [ ] Step 2.3 — verify with a local `:dokkaGenerate` and a visual check of the
      home page plus two modules; deploy rides the next tag.
- [x] Delete the merged files (`WEB_MVC.md`, `ACCESS.md`, `OPENAPI.md`,
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
- [ ] Tag the release so JitPack builds versioned artifacts with sources and
      javadoc; documentation deploys via `docs.yml`.

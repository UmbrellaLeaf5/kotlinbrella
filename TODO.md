# Kotlinbrella Implementation Plan

This document is the implementation backlog and architecture record for
Kotlinbrella. Complete phases in order unless a later item explicitly depends
on a stable earlier public contract. Do not publish an artifact whose public
API lacks unit and integration coverage.

Done items are deleted, not ticked: if it is not listed here, it is shipped.

## Phase 1: Slicer API Migration (staged, green suite after each step)

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

## Phase 2: Future Extensions

- [ ] Spring Security integration based on authenticated principals, never IDs
      supplied in request bodies.
- [ ] Metrics, tracing, and structured logging extensions.
- [ ] KSP research only after two applications share a stable interface-to-delegate
      contract; do not begin with a reflection code generator.

## Phase 3: Documentation as One System (GUIDE.md + Pages, no duplication)

Principle: each knowledge type has exactly one home. `GUIDE.md` is the
narrative (why, when, how to configure, recipes, migration notes).
The Pages site (Dokka) is the reference (what: packages, classes, functions).
`GUIDE.md` links into Dokka pages; KDoc links out to `GUIDE.md` sections where
behavior needs a narrative. No prose is duplicated between them.

- [ ] Step 3.1 — Dokka curation: `package.md` per public package, suppress
      `*internal*` packages from navigation, README includes on the aggregate
      page, footer with the tag version plus GitHub link,
      `suppressObviousFunctions`, and a KDoc top-up for uncovered public API.
- [ ] Step 3.2 — `GUIDE.md` organized by use cases (errors in both shapes,
      validation, access, JPA, OpenAPI, conversions, samples), with links into
      the Dokka reference; trim `README.md` to a front page with links.
      Document the library policy here (`ApiException` family for runtime
      client failures; `IllegalStateException`/`IllegalArgumentException` for
      configuration failures).
- [ ] Step 3.3 — verify with a local `:dokkaGenerate` and a visual check of the
      home page plus two modules; deploy rides the next tag.

## Release Checklist (recurring, run on every release)

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

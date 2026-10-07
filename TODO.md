# Kotlinbrella Implementation Plan

This document is the implementation backlog and architecture record for
Kotlinbrella. Complete phases in order unless a later item explicitly depends
on a stable earlier public contract. Do not publish an artifact whose public
API lacks unit and integration coverage.

Done items are deleted, not ticked: if it is not listed here, it is shipped.

## Phase 1: Future Extensions

- [ ] Spring Security integration based on authenticated principals, never IDs
      supplied in request bodies.
- [ ] Metrics, tracing, and structured logging extensions.
- [ ] KSP research only after two applications share a stable interface-to-delegate
      contract; do not begin with a reflection code generator.

## Phase 2: Error-Docs Customizer in the Library, Root Parity

Direction: root is the reference implementation. Slicer-api is aligned to it
where the domains allow. Rendering of error documentation is driven by
`kotlinbrella.web.error-shape`: `simple` renders the `{error, message}`
style, `standard` the RFC 9457 style. No new configuration keys.

Slicer-api vs root comparison (verified against code and the 162-test suite):

- Controllers: parity. Thin single-call methods, `*String` boundaries with
  explicit URL names, library `IdResponse` on both sides.
- Services: parity on boundaries (`*String` converted first thing).
  `CalculationService` (528 lines, whole domain workflow inline, no
  `internal/`) is accepted tech debt — no refactor without a functional
  reason. Root additionally splits the deepest flows into `internal/`.
- Library joints parity: `ApiException` family with `.unified()`,
  `findByIdOrThrow`, `checkNotNull*`, `toUUID/toEnumOrThrow`,
  `error-shape: simple` with `mode` per profile, JPA advice enabled,
  request-id filter defaults, no `mavenLocal` anywhere.
- Access: root uses `CheckOwnership`; slicer-api has none by design
  (internal service, no user scoping).
- OpenAPI specs: root uses library `@ApiError`/`@ApiErrors` plus its own
  `ApiSpecErrorOperationCustomizer` (library autoconfiguration excluded).
  Slicer-api uses manual per-endpoint `@ApiResponse` blocks with
  `SimpleErrorResponse` schema (library autoconfiguration enabled, verified
  no duplication in `/api-docs`).
- Validation gap (closed by design, not debt): root validates 5 DTOs plus
  specs (`RequiredField`, `ValidEmail`, `ValidUUID`, `ValidEnum`, `@Size`,
  `AtLeastOnePresent`, domain validators). Slicer-api has one request DTO:
  `RequiredField` + `ValidUUID` on `listingId`, `RequiredField` on
  `name`/`presignedUrl`; status filter converts at runtime exactly like root
  does. No PATCH/email/numeric fields exist in slicer-api, so the remaining
  annotations do not apply.

Work:

- [ ] Step 2.1 — library: rewrite `KotlinbrellaErrorOperationCustomizer` as
      shape-aware (inject `KotlinbrellaWebProperties`, no new keys). SIMPLE:
      `application/json`, `$ref SimpleErrorResponse`, bullet-merged
      descriptions, single error becomes `example {error, message}`, several
      become an `examples` dropdown. STANDARD: `application/problem+json`,
      Problem schema, same single/multi example rule. Register the
      `SimpleErrorResponse` component in `KotlinbrellaSchemaCustomizer` for
      the SIMPLE shape. Cover both shapes (single/multi/merge) with tests.
- [ ] Step 2.2 — root: delete `ApiSpecErrorOperationCustomizer` and
      `shared/data/api/errors/ErrorResponse.kt`, drop the
      `KotlinbrellaOpenApiAutoConfiguration` exclude; `/api-docs` diff must
      show only the schema rename (`ErrorResponse` to `SimpleErrorResponse`);
      full suite green.
- [ ] Step 2.3 — tag `0.1.1`, bump the version in root and slicer-api,
      full suite green.
- [ ] Step 2.4 — slicer-api: replace the manual error blocks in
      `CalculationApiSpec` with `@ApiErrors` (manual plus generated entries
      on the same statuses would duplicate descriptions); full suite green.
- [ ] Step 2.5 — slicer-api alignment leftovers: `@Size(255)` caps on
      `CalculationRequest` strings; resolve the dormant `@ValidUUID` on the
      batch-delete body (remove it or make element validation fire —
      the service conversion already guards with 400).

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

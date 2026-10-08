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

## Phase 2: Shape-Aware Error Docs, Slicer-api Adoption

Direction: one shape-aware error-docs customizer in the library, driven by
`kotlinbrella.web.error-shape` with no new configuration keys. Slicer-api
adopts `@ApiError`/`@ApiErrors` exactly like root does. Root migration stays
with the service owner and is out of scope here, as is any service-side work
(thick services, validators, health, limits, reconciliation).

Work:

- [ ] Step 2.1 — library: rewrite `KotlinbrellaErrorOperationCustomizer` as
      shape-aware (inject `KotlinbrellaWebProperties`). Both shapes merge
      `• detail` bullets into the response description. SIMPLE:
      `application/json`, `$ref SimpleErrorResponse`, single error becomes
      `example {error, message}`, several become an `examples` dropdown keyed
      by detail. STANDARD: `application/problem+json`, `$ref
      KotlinbrellaProblem`, same single/multi example rule. Register the
      `SimpleErrorResponse` component in `KotlinbrellaSchemaCustomizer` for
      the SIMPLE shape. Cover both shapes (single/multi/merge) with tests.
- [ ] Step 2.2 — tag `0.2.0`, bump the version in slicer-api.
- [ ] Step 2.3 — slicer-api: replace the manual error blocks in
      `CalculationApiSpec` with `@ApiErrors` (manual plus generated entries
      on the same statuses would duplicate descriptions); suite green.

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

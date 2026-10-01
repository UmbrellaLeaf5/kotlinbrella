# OpenAPI Starter

`kotlinbrella-spring-boot-starter-openapi` includes Springdoc's Web MVC UI.
Annotate controller endpoints or API-spec interfaces with
`@ApiErrors([ApiError(code = "BAD_REQUEST", detail = "Invalid input")])`.
The HTTP status is derived from the code through `ErrorCode.status(...)`,
exposed to Kotlin call sites as `apiError.status`, and the reason phrase
through `apiError.title`, so the annotation works in Kotlin and Java
without duplicated status values.
Codes are the stable uppercase values from Kotlinbrella's error contract;
examples use the same `type`, `title`, `status`, `detail`, `instance`, `code`,
and `traceId` fields returned by MVC. Validation problems also include
`violations` with `field`, `message`, and `code`.

The customizer adds examples by status/code and preserves existing response
descriptions, media types, schemas, and application examples. The canonical
schemas are registered once under `KotlinbrellaProblem` and
`KotlinbrellaViolation`. API-spec interfaces are supported and recommended,
not required. The Web MVC starter works without Springdoc.

For an application retaining an older, versioned error response shape, set
`kotlinbrella.openapi.enabled=false` and provide an application-specific
`OperationCustomizer` for the library annotations. This keeps documentation
consistent with its actual HTTP response until the API contract is migrated.

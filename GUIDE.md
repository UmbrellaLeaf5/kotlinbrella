# Kotlinbrella Guide

Narrative guide: why the suite exists, when to use each starter, how to
configure it, and what changes between releases.

- **This guide** — use cases, recipes, configuration, migration notes.
- **[Dokka reference](https://umbrellaleaf5.github.io/kotlinbrella/)** — the API
  catalogue: packages, classes, functions, signatures.
- **[README](README.md)** — title page, modules, and dependency coordinates.

## Contents

- [Getting started](#getting-started)
- [Error contract](#error-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Ownership checks](#ownership-checks)
- [Spring Data JPA](#spring-data-jpa)
- [OpenAPI documentation](#openapi-documentation)
- [Conversions and null checks](#conversions-and-null-checks)
- [Testing docs against runtime](#testing-docs-against-runtime)
- [Library policy](#library-policy)
- [Migration notes](#migration-notes)

## Getting started

Kotlinbrella requires Kotlin 2.x, Java 21, and Spring Boot 4.x. Consume it from
JitPack:

```kotlin
repositories {
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.UmbrellaLeaf5.kotlinbrella:kotlinbrella-spring-boot-starter:<tag>")
}
```

Use the aggregate starter for the full suite, or a focused starter when a
service needs only part of it. Auto-configuration is conditional: adding a
starter without the matching framework dependency changes nothing.

Runnable examples live in `samples/`:

- `samples/full-mvc` — JPA, access checks, Springdoc, Liquibase.
- `samples/minimal-mvc` — MVC errors and request identifiers only.
- `samples/java-consumer` — the public API from Java.

## Error contract

Every client-facing error has one message. It is informative on purpose: the
former diagnostic text is now the only text. There is no hidden/public split
and no profile-dependent branching. The one exception is an unexpected server
failure, which always returns a generic `Internal server error` message and
logs the cause.

Two shapes are available through `kotlinbrella.web.error-shape`.

### `SIMPLE` — `{error, message}`

`error` is the HTTP reason phrase, `message` is the details. Validation errors
are merged into `message` as `field: reason; ...`.

```json
{
  "error": "Not Found",
  "message": "Order with ID 42 not found"
}
```

### `STANDARD` — RFC 9457 problem details

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Order with ID 42 not found",
  "instance": "/api/v1/orders/42",
  "code": "NOT_FOUND",
  "traceId": "0f8fad5b-d9cb-469f-a165-70867728950e"
}
```

Validation failures add a `violations` array with `field`, `message`, and
`code` entries; `field` is `null` for object-level errors.

### Throwing errors

Throw the `ApiException` family; the advice renders it in the configured shape.

```kotlin
throw NotFoundException("Order with ID $orderId not found")
throw ConflictException("Order is already being processed")
throw BadRequestException("Quantity must be positive")
```

Each subclass takes `(message, code = <its default code>, cause = null)`:

| Exception                     | Status |
| ----------------------------- | ------ |
| `BadRequestException`         | 400    |
| `ForbiddenException`          | 403    |
| `NotFoundException`           | 404    |
| `ConflictException`           | 409    |
| `PayloadTooLargeException`    | 413    |
| `InternalServerException`     | 500    |
| `ServiceUnavailableException` | 503    |

Use the `code` argument only when the default code is not precise enough, and
keep it in `ErrorCode`.

### Framework-handled failures

The MVC advice also maps failures raised before a controller runs:

| Situation                                      | Status | Code                    |
| ---------------------------------------------- | ------ | ----------------------- |
| Body or method validation, constraint violation | 400    | `BAD_REQUEST`           |
| Unreadable body, type mismatch, missing value   | 400    | `BAD_REQUEST`           |
| Unknown route or static resource                | 404    | `NOT_FOUND`             |
| Unsupported method                              | 405    | `METHOD_NOT_ALLOWED`    |
| Unsupported media type                          | 415    | `UNSUPPORTED_MEDIA_TYPE`|
| Unexpected exception                            | 500    | `INTERNAL_ERROR`        |

The JPA advice maps `OptimisticLockingFailureException` and
`DataIntegrityViolationException` to `409 Conflict` with the exception message,
and `PropertyReferenceException` to `400 Bad Request` naming the unknown sort
property. Those details intentionally stay visible: they describe the client's
request, not server internals.

Request identifiers are read from `kotlinbrella.web.request-id-header` and
echoed as `traceId`; a missing header is replaced by a generated UUID.

## Configuration

```yaml
kotlinbrella:
  web:
    enabled: true
    request-id-header: X-Request-Id
    error-shape: STANDARD   # or SIMPLE
    force-error-schema: false
  access:
    enabled: true
    resource-id-template: '{resource}IdString'
    user-id-parameter: userIdString
```

| Property                              | Default          | Meaning |
| ------------------------------------- | ---------------- | ------- |
| `kotlinbrella.web.enabled`            | `true`           | MVC advice, filter, OpenAPI customizers. |
| `kotlinbrella.web.request-id-header`  | `X-Request-Id`   | Incoming trace header. |
| `kotlinbrella.web.error-shape`        | `STANDARD`       | `STANDARD` (RFC 9457) or `SIMPLE` (`{error, message}`). |
| `kotlinbrella.web.force-error-schema` | `false`          | Overwrite a foreign schema on documented error responses instead of keeping it with a warning. |
| `kotlinbrella.access.enabled`         | `true`           | Ownership aspect and registry. |
| `kotlinbrella.access.resource-id-template` | `{resource}IdString` | Parameter name template for the resource ID. |
| `kotlinbrella.access.user-id-parameter` | `userIdString` | Parameter name for the caller ID. |

Keep `force-error-schema` at `false` unless a service deliberately overrides
the error schema: the default keeps the existing schema and logs a warning, so
a mismatch is visible without silently rewriting somebody else's contract.

## Validation

The validation starter provides explicit constraints for request DTO fields:

| Annotation            | Purpose |
| --------------------- | ------- |
| `@RequiredField`      | Non-null, non-blank text. |
| `@ValidUUID`          | UUID-shaped text. |
| `@ValidEmail`         | Email-shaped text. |
| `@ValidEnum(values)`  | One of the listed names; the message lists allowed values. |
| `@ValidPositiveInt`   | Positive integer text, `allowZero` optional. |
| `@ValidPositiveDouble`| Positive finite double text, `allowZero` optional. |
| `@ValidBatchIds`      | Required, non-empty collection within `max`; elements keep their own constraints. |
| `@AtLeastOnePresent(properties)` | Patch semantics: at least one listed property must be present. |

```kotlin
data class OrderPatchRequest(
  @field:JsonProperty("quantity")
  @field:ValidPositiveInt
  val quantity: String? = null,

  @field:JsonProperty("comment")
  val comment: String? = null,
)
```

`@AtLeastOnePresent` treats blank strings, empty collections and empty maps as
absent, while `0` and `false` count as present. `@ValidBatchIds` is
intentionally limited to required batches; optional batches need a separate
check.

Validation failures are collected into one response; do not catch them in
controllers.

## Ownership checks

The access starter separates decisions from delivery. Register one
`AccessChecker` bean per resource:

```kotlin
@Component
class OrderAccessChecker(
  private val orderRepository: OrderRepository,
) : AccessChecker {

  override val resource = "order"

  override fun check(resourceIdString: String, userIdString: String): AccessDecision {
    val order = orderRepository.findById(resourceIdString.toUUIDOrNull() ?: return AccessDecision.NOT_FOUND)
      ?: return AccessDecision.NOT_FOUND

    return if (order.userId == userIdString.toUUIDOrNull()) AccessDecision.ALLOWED
      else AccessDecision.FORBIDDEN
  }
}
```

Annotate the controller method and let the aspect resolve the raw ID parameters
by name:

```kotlin
@CheckOwnership(resource = "order")
@GetMapping("/orders/{order_id}")
fun getOrder(
  @RequestParam("user_id") userIdString: String,
  @PathVariable("order_id") orderIdString: String,
): OrderResponse = orderService.getOrder(userIdString, orderIdString)
```

Parameter names default to `{resource}IdString` and `userIdString`; override
them per annotation with `resourceIdParam` / `userIdParam`. A denial returns
`404 Not Found` by default (`DenialPolicy.HIDE_EXISTENCE`); use
`DenialPolicy.FORBIDDEN` to return `403` instead. A missing checker or a
mistyped parameter name fails fast at runtime as a configuration error.

## Spring Data JPA

```kotlin
val order = orderRepository.findByIdOrThrow(orderId, "Order")
```

`findByIdOrThrow` composes `NotFoundException` with a `"<Entity> with ID <id>
not found"` message. It works with any `CrudRepository` ID type and does not
touch lazy associations.

## OpenAPI documentation

Declare errors next to the endpoint:

```kotlin
@ApiErrors(
  ApiError(ErrorCode.NOT_FOUND, "Order with ID 42 not found"),
  ApiError(ErrorCode.BAD_REQUEST, "Invalid quantity"),
)
@GetMapping("/orders/{order_id}")
fun getOrder(...): OrderResponse
```

The customizer merges the codes into the operation's responses in the active
shape, dedupes identical messages, and keeps foreign media types and schemas out
of the active shape. `ApiErrorCodeValidator` checks every declared code at
startup, so a typo fails fast. It validates MVC mappings only; actuator
endpoints are out of scope.

For services that override the error schema deliberately, set
`force-error-schema: true`; otherwise the existing schema is preserved and a
warning names the mismatch.

## Conversions and null checks

Use conversions at the service boundary, before any business logic:

```kotlin
fun getOrder(userIdString: String, orderIdString: String): OrderResponse {
  val userId = userIdString.toUUIDOrThrow()
  val orderId = orderIdString.toUUIDOrThrow()
  // ...
}
```

`toUUIDOrThrow`, `toInstantOrThrow`, `toIntOrThrow`, `toLongOrThrow`,
`toDoubleOrThrow`, and `toEnumOrThrow` throw `BadRequestException` with a
specific `ErrorCode`. Nullable variants (`toUUIDOrNull`, ...) return `null`
instead. `toDoubleOrThrow` rejects non-finite values.

For invariants inside the application, use the named null checks:
`checkNotNullByName` / `checkFieldNotNullByName` for entity state,
`requireNotNullByName` / `requireFieldNotNullByName` for input. They throw
`IllegalStateException` / `IllegalArgumentException` with the field name and
never throw `NullPointerException`.

## Testing docs against runtime

`kotlinbrella-test` keeps generated docs honest:

```kotlin
ErrorDocsParity.assertParity(
  mockMvc = mockMvc,
  openApiJson = mockMvc.perform(get("/v3/api-docs")).andReturn().response.contentAsString,
  method = "get",
  path = "/api/v1/orders/{order_id}",
  probes = mapOf(404 to "/api/v1/orders/00000000-0000-0000-0000-000000000000"),
)
```

It performs a request using the documented method, compares every documented
example field against the real body with strict JSON equality (so `404` and
`"404"` differ), and fails on unexpected runtime fields unless
`strict = false`. Supported methods: `get`, `post`, `put`, `patch`, `delete`.

## Library policy

- Runtime client failures are always the `ApiException` family; framework
  failures are translated by the advice, never by controllers.
- Configuration errors use `IllegalStateException` / `IllegalArgumentException`;
  they are not client errors and must not be rendered as `4xx`.
- Validation annotations validate request text only. Domain state belongs to
  entities and services.
- Accessibility of `ApiErrorCodeValidator` is limited to MVC mappings;
  actuator endpoints are out of scope.
- `@ValidBatchIds` covers required, non-empty batches within the limit;
  optional batches are checked separately.
- `force-error-schema` defaults to `false` (keep and warn). Enable it only with
  a deliberate reason.

## Migration notes

### From 0.3.x to the next release

This release removes the hidden/public message split.

- `ApiException` exposes a single `detail` property (formerly `publicDetail`).
  `diagnosticDetail` is gone.
- Subclass constructors take `(message, code, cause)`. The two-argument
  `(devMessage, prodMessage)` constructors are gone.
- `unified()` factories are gone. Throw `ConflictException("...")` directly.
- `kotlinbrella.web.mode` is gone. Remove it from all profiles. The advice
  always returns the informative message; unexpected server failures stay
  generic.
- `KotlinbrellaWebProperties.mode` and the `Mode` helpers (`exposeDiagnostics`,
  `verbose`) are gone.
- Repository lookups always include the identifier in the message.
- JPA conflicts expose the persistence exception message.

Replace:

```kotlin
throw ConflictException(devMessage = "Order is being processed", prodMessage = "Conflict")
throw ConflictException.unified("Order is being processed")
```

with:

```kotlin
throw ConflictException("Order is being processed")
```

If a client relied on the short public text, update its expectations to the
informative message. Python contract tests in Digital Factory already assert
that text.

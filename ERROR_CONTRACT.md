# Error Contract

Kotlinbrella errors use `application/problem+json` and RFC 9457 semantics.
`type` is `about:blank` for standard HTTP problems; custom codes are stable,
uppercase ASCII identifiers and are supplied in `code`. `title` is the HTTP
status phrase, `status` is the numeric HTTP status, and `detail` is a safe
public description. `instance` is the request path, without query parameters.
`traceId` is an opaque correlation identifier and `violations` is a list of
`{field, message, code}` objects (`field` is null for global errors).

All messages are supplied by applications in their chosen language; the
library does not translate messages. Diagnostic details are exposed only in
`dev` mode, selected per profile through application configuration. Codes
and JSON names are versioned public contracts. Unknown errors must never
expose exception text.

Applications on a compact contract use the `simple` error shape:
`{error, message}`, where `error` is the reason phrase and `message` is the
same public detail as the standard shape (validation violations joined with
`; `).

Library policy: runtime client failures are always the `ApiException`
family (`BadRequestException`, `NotFoundException`, `ConflictException`,
`ForbiddenException`, `PayloadTooLargeException`, `ServiceUnavailableException`,
`InternalServerException`), at any status. Configuration failures are
`IllegalStateException` or `IllegalArgumentException` and never cross the
HTTP boundary as client errors. Domain exceptions must extend the
`ApiException` family to be rendered; vendor-specific failures (S3, queues,
brokers) stay outside the library.

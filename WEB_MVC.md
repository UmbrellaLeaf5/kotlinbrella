# Web MVC Starter

Add `kotlinbrella-spring-boot-starter-webmvc` to a Servlet MVC application.
The advice is enabled by default, has lowest precedence so application advice
can override individual mappings, and emits `application/problem+json` with
the fields documented in [ERROR_CONTRACT.md](ERROR_CONTRACT.md).

| Property | Default | Purpose |
| --- | --- | --- |
| `kotlinbrella.web.enabled` | `true` | Disable auto-configuration explicitly. |
| `kotlinbrella.web.expose-debug-details` | `false` | Expose diagnostic exception details. |
| `kotlinbrella.web.diagnostic-profiles` | empty | Profiles allowed to expose diagnostics. |
| `kotlinbrella.web.request-id-header` | `X-Request-Id` | Correlation header. |
| `kotlinbrella.web.log-expected4xx` | `false` | Log expected 4xx codes without request values. |

The filter accepts caller-supplied IDs matching `[A-Za-z0-9_-]{1,64}` and
replaces other values with random UUIDs. It returns the ID in the response,
sets `traceId` in the error body, and scopes MDC to the request. Do not treat
incoming IDs as authenticated identities. The `instance` is the URI path
without query parameters. Expected 4xx logging includes only an error code;
unknown exceptions are logged once with a stack trace and rendered as a safe
500 without their exception text.

In Digital Factory, existing application advice retains its behavior while it
has higher precedence. Migrate its mappings only after comparing the existing
`error`/`message` JSON against the RFC 9457 response contract.

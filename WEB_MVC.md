# Web MVC Starter

Add `kotlinbrella-spring-boot-starter-webmvc` to a Servlet MVC application.
The advice is enabled by default, has lowest precedence so application advice
can override individual mappings, and emits `application/problem+json` with
the fields documented in [ERROR_CONTRACT.md](ERROR_CONTRACT.md).

| Property | Default | Purpose |
| --- | --- | --- |
| `kotlinbrella.web.enabled` | `true` | Disable auto-configuration explicitly. |
| `kotlinbrella.web.expose-debug-details` | `false` | Expose diagnostic exception details. |
| `kotlinbrella.web.diagnostic-profiles` | empty | Profile names allowed to expose diagnostics. |
| `kotlinbrella.web.request-id-header` | `X-Request-Id` | Correlation header. |
| `kotlinbrella.web.log-expected4xx` | `false` | Log expected 4xx failures. |
| `kotlinbrella.web.error-shape` | `problem` | `problem` for RFC 9457, `legacy` for `{error, message}`. |
| `kotlinbrella.web.log-level` | `info` | Minimum level for advice-emitted logs (`trace`..`error`). |

The filter accepts caller-supplied IDs matching `[A-Za-z0-9_-]{1,64}` and
replaces other values with random UUIDs. It returns the ID in the response,
sets `traceId` in the error body, and scopes MDC to the request. Do not treat
incoming IDs as authenticated identities. The `instance` is the URI path
without query parameters.

Diagnostics, verbosity, and logging form two independent dimensions that
compose with Spring profiles (for example per-profile YAML files):
diagnostics decide *what* may be exposed (dev shows diagnostic details and
full log records, prod shows public details and one-line records), while
`log-level` decides which advice-emitted records are written at all (`error`
keeps only error logs and drops info records). Unknown 5xx failures are always
logged once with a stack trace and rendered as a safe 500 without their
exception text, regardless of both settings. Rejected values and secrets are
never logged.

In `legacy` shape the body is `{error, message}` where `error` is the reason
phrase and `message` is the public detail (validation violations joined with
`; `); no problem schemas are registered.

## Example Usage

```yaml
# application-prod.yaml: quiet production with public messages
kotlinbrella:
  web:
    log-level: error
```

```yaml
# application-test.yaml: full trace with diagnostic messages
kotlinbrella:
  web:
    expose-debug-details: true
    log-expected4xx: true
    log-level: trace
```

```yaml
# application-analytics.yaml: informative but public messages
kotlinbrella:
  web:
    log-expected4xx: true
    log-level: info
```

```yaml
# application-legacy.yaml: versioned legacy error contract
kotlinbrella:
  web:
    error-shape: legacy
```

# Core Utilities

`requireNotNullByName` and `requireFieldNotNullByName` reject missing caller
inputs with `IllegalArgumentException`. `checkNotNullByName` and
`checkFieldNotNullByName` report broken internal state with
`IllegalStateException`. Name lambdas are evaluated only on failure; they can
include any identifier type without assuming UUID. Object-scoped variants
accept any identifier type as well. Java callers use `NullChecksFacade`
(static methods with plain `String` names) and `Conversions` instead of the
Kotlin extensions.

Conversion extensions accept no surrounding whitespace: callers must trim
explicitly if desired. Numeric overflow fails with a `BadRequestException`;
doubles must be finite. Instants require an ISO-8601 offset/UTC representation
accepted by `Instant.parse`. Enums are case-sensitive. Null inputs must be
handled before calling these non-null `String` extensions. Diagnostics may
contain the rejected value for UUID/instant; public details never do.

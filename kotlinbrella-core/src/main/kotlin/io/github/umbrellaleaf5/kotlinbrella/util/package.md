# Package io.github.umbrellaleaf5.kotlinbrella.util

Parsing and invariant helpers for the service boundary.

Conversion functions turn raw request strings into typed values and throw
`BadRequestException` with a precise code; nullable variants return `null`.
Named null checks fail with a readable message and the field name instead of a
bare `NullPointerException`. See the
[conversions guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#conversions-and-null-checks).

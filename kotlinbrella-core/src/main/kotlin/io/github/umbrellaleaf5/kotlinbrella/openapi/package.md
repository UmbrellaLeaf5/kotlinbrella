# Package io.github.umbrellaleaf5.kotlinbrella.openapi

Documentation annotations and helpers for the error contract.

`ApiErrors` declares client-visible failures on an endpoint. `ApiError` pairs a
code from `ErrorCode` with the message the endpoint can return; its `status`
and `title` extensions resolve the HTTP semantics used by the OpenAPI
customizer. See the
[OpenAPI guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#openapi-documentation).

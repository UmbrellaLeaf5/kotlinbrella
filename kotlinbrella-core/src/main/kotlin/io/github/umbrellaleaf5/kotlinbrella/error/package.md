# Package io.github.umbrellaleaf5.kotlinbrella.error

Client error contract shared by every Kotlinbrella starter.

`ApiException` is the single base type for runtime client failures. Each
subclass fixes an HTTP status and a default `ErrorCode`; the `detail` string is
returned to the client as-is. There is no hidden/public message split — see the
[error guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#error-contract) for response
shapes and examples.

Framework failures are translated by the MVC and JPA advice, not by
controllers. Throw these exceptions from services and let the advice render
them.

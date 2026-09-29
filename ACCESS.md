# Access and Ownership Starter

Add `kotlinbrella-spring-boot-starter-access` and provide one Spring bean
implementing `AccessChecker` per stable resource key. The application owns
all existence/ownership queries; prefer an efficient query such as
`existsByIdAndUserId` rather than loading entities and navigating relations.
Checkers receive raw `resourceIdString` and `userIdString` values and must
validate/convert them, returning `ALLOWED`, `NOT_FOUND` or `FORBIDDEN`.
Malformed IDs should throw a Kotlinbrella bad request through a conversion
helper. If the application must distinguish a missing user, the checker must
check that user before the resource and return `NOT_FOUND`.

Annotate a proxied service implementation method:

```kotlin
@CheckOwnership(resource = "listing")
fun getListing(userIdString: String, listingIdString: String): ListingResponse = ...
```

By default the aspect finds `{resource}IdString` and `userIdString`, using
`kotlinbrella.access.resource-id-template` and
`kotlinbrella.access.user-id-parameter`. For other method signatures, set
`resourceIdParam` and `userIdParam` on the annotation. `NOT_FOUND` always
produces 404. `FORBIDDEN` produces 404 by default to conceal existence;
`policy = DenialPolicy.FORBIDDEN` produces 403. A missing checker or argument
raises an actionable configuration error; duplicate checkers fail startup.
Set `kotlinbrella.access.enabled=false` to disable registration.

Spring AOP intercepts external proxy calls to implementation methods. A
method invoked from another method on the same instance bypasses the proxy.
Use `open` methods/classes or Kotlin's Spring plugin. Place the annotation on
the implementation method: with class-based proxies an interface-only
annotation is not intercepted. Decision logs include only the resource key
and decision, never identifiers or request bodies. For mutations, an access
check and the later database update must be coordinated transactionally when
TOCTOU races matter; an aspect alone cannot guarantee atomic ownership.

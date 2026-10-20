# Package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

Conditional Spring Boot configuration for every focused starter.

Each auto-configuration activates only when its framework dependency is on the
classpath. Properties live under `kotlinbrella.web` and `kotlinbrella.access`;
see the [configuration guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#configuration).
The MVC advice renders the single informative client message in the selected
`ErrorShape`, the JPA advice maps persistence conflicts, and the OpenAPI
customizers describe the same contract that runs in production.

These classes are wiring, not application API. Prefer the starters and the
public contracts in `kotlinbrella-core`.

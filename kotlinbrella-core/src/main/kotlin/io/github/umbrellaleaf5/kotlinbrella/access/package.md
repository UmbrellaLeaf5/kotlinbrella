# Package io.github.umbrellaleaf5.kotlinbrella.access

Declarative ownership checks that stay independent from application entities.

Applications register an `AccessChecker` bean per resource and annotate
controller methods with `CheckOwnership`. The aspect resolves raw identifier
arguments and applies the configured `DenialPolicy`. See the
[ownership guide](https://github.com/UmbrellaLeaf5/kotlinbrella/blob/main/GUIDE.md#ownership-checks).

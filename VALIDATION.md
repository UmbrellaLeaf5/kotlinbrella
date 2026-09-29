# Validation Starter

Add `kotlinbrella-spring-boot-starter-validation` for Jakarta Bean Validation
and the neutral class-level `@AtLeastOnePresent(properties = ["name", "status"])`
constraint. List only patchable fields: unrelated technical fields do not
make an empty patch valid. The violation has stable code
`AT_LEAST_ONE_PRESENT` and an interpolated default message. An unknown or
duplicate property is a configuration error, not a silently passing request.

Null, blank text, empty collections/maps/arrays are absent. Zero and false
are present. The constraint accepts Kotlin getter properties, JavaBeans and
Java records without using `kotlin-reflect`. A null object is valid so that
the caller's `@NotNull` can separately enforce its presence. Other constraints
should use standard Hibernate Validator annotations.

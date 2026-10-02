package io.github.umbrellaleaf5.kotlinbrella.util

// MARK: Named null messages
// --------------------------------------------------

fun valueCannotBeNull(name: String): String = "$name cannot be null"

// --------------------------------------------------

fun valueCannotBeNullInObject(
  name: String,
  objectName: String,
  objectId: Any,
): String = "${valueCannotBeNull(name)} in $objectName: $objectId"

// MARK: Named input checks
// --------------------------------------------------

fun <T> T?.requireNotNullByName(lazyName: () -> String): T & Any =
  requireNotNull(this) { valueCannotBeNull(lazyName()) }

// --------------------------------------------------

fun <T> T?.requireNotNullByNameInObject(
  name: String,
  objectName: String,
  objectId: Any,
): T & Any = requireNotNull(this) {
  valueCannotBeNullInObject(name, objectName, objectId)
}

// --------------------------------------------------

inline fun <reified T : Any> T?.requireFieldNotNullByName(noinline lazyName: () -> String): T =
  requireNotNullByName { "${T::class.simpleName}.${lazyName()}" }

// --------------------------------------------------

inline fun <reified T : Any> T?.requireFieldNotNullByNameInObject(
  name: String,
  objectName: String,
  objectId: Any,
): T = requireNotNullByNameInObject(
  "${T::class.simpleName}.$name",
  objectName,
  objectId,
)

// MARK: Named state checks
// --------------------------------------------------

fun <T> T?.checkNotNullByName(lazyName: () -> String): T & Any =
  checkNotNull(this) { valueCannotBeNull(lazyName()) }

// --------------------------------------------------

fun <T> T?.checkNotNullByNameInObject(
  name: String,
  objectName: String,
  objectId: Any,
): T & Any = checkNotNull(this) {
  valueCannotBeNullInObject(name, objectName, objectId)
}

// --------------------------------------------------

inline fun <reified T : Any> T?.checkFieldNotNullByName(noinline lazyName: () -> String): T =
  checkNotNullByName { "${T::class.simpleName}.${lazyName()}" }

// --------------------------------------------------

inline fun <reified T : Any> T?.checkFieldNotNullByNameInObject(
  name: String,
  objectName: String,
  objectId: Any,
): T = checkNotNullByNameInObject(
  "${T::class.simpleName}.$name",
  objectName,
  objectId,
)

package io.github.umbrellaleaf5.kotlinbrella.util

object NullChecksFacade {

  // MARK: Java null-check facade
  // --------------------------------------------------

  @JvmStatic
  fun valueCannotBeNull(name: String): String =
    io.github.umbrellaleaf5.kotlinbrella.util.valueCannotBeNull(name)

  // --------------------------------------------------

  @JvmStatic
  fun valueCannotBeNullInObject(name: String, objectName: String, objectId: Any): String =
    io.github.umbrellaleaf5.kotlinbrella.util.valueCannotBeNullInObject(name, objectName, objectId)

  // --------------------------------------------------

  @JvmStatic
  fun <T> requireNotNullByName(value: T?, name: String): T & Any =
    value.requireNotNullByName { name }

  // --------------------------------------------------

  @JvmStatic
  fun <T> requireNotNullByNameInObject(
    value: T?,
    name: String,
    objectName: String,
    objectId: Any,
  ): T & Any = value.requireNotNullByNameInObject(name, objectName, objectId)

  // --------------------------------------------------

  @JvmStatic
  fun <T> requireFieldNotNullByName(value: T?, type: Class<*>, name: String): T & Any =
    value.requireNotNullByName { "${type.simpleName}.$name" }

  // --------------------------------------------------

  @JvmStatic
  fun <T> requireFieldNotNullByNameInObject(
    value: T?,
    type: Class<*>,
    name: String,
    objectName: String,
    objectId: Any,
  ): T & Any = value.requireNotNullByNameInObject(
    "${type.simpleName}.$name",
    objectName,
    objectId,
  )

  // --------------------------------------------------

  @JvmStatic
  fun <T> checkNotNullByName(value: T?, name: String): T & Any =
    value.checkNotNullByName { name }

  // --------------------------------------------------

  @JvmStatic
  fun <T> checkNotNullByNameInObject(
    value: T?,
    name: String,
    objectName: String,
    objectId: Any,
  ): T & Any = value.checkNotNullByNameInObject(name, objectName, objectId)

  // --------------------------------------------------

  @JvmStatic
  fun <T> checkFieldNotNullByName(value: T?, type: Class<*>, name: String): T & Any =
    value.checkNotNullByName { "${type.simpleName}.$name" }

  // --------------------------------------------------

  @JvmStatic
  fun <T> checkFieldNotNullByNameInObject(
    value: T?,
    type: Class<*>,
    name: String,
    objectName: String,
    objectId: Any,
  ): T & Any = value.checkNotNullByNameInObject(
    "${type.simpleName}.$name",
    objectName,
    objectId,
  )

}

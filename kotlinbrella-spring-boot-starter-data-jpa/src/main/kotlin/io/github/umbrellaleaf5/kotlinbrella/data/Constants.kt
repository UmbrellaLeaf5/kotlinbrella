package io.github.umbrellaleaf5.kotlinbrella.data

object Constants {

  object ErrorDescription {

    const val WITH_ID = " with ID "
    const val NOT_FOUND_SUFFIX = " not found"

    // --------------------------------------------------

    fun notFound(entityName: String): String = "$entityName$NOT_FOUND_SUFFIX"

    // --------------------------------------------------

    fun notFoundWithId(entityName: String, id: Any): String =
      "$entityName$WITH_ID$id$NOT_FOUND_SUFFIX"

  }

}

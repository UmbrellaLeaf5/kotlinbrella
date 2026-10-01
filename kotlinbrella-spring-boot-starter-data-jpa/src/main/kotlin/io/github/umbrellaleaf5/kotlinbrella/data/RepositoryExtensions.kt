package io.github.umbrellaleaf5.kotlinbrella.data

import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException
import org.springframework.data.repository.CrudRepository

// MARK: Look up a resource by any identifier type
// --------------------------------------------------

fun <T : Any, ID : Any> CrudRepository<T, ID>.findByIdOrThrow(
  id: ID,
  entityName: String,
): T = findById(id).orElseThrow {
  NotFoundException(
    Constants.ErrorDescription.notFoundWithId(entityName, id),
    Constants.ErrorDescription.notFound(entityName),
  )
}

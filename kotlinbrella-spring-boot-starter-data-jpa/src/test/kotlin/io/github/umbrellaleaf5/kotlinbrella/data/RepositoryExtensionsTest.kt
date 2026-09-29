package io.github.umbrellaleaf5.kotlinbrella.data

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.repository.CrudRepository
import java.util.Optional

class RepositoryExtensionsTest {

  // MARK: Support non-UUID repository identifiers
  // --------------------------------------------------

  @Test
  fun findsEntitiesWithLongIdentifiers() {
    @Suppress("UNCHECKED_CAST")
    val repository = mock(CrudRepository::class.java) as CrudRepository<String, Long>
    `when`(repository.findById(42L)).thenReturn(Optional.of("result"))

    assertEquals("result", repository.findByIdOrThrow(42L, "Item"))
  }

  // --------------------------------------------------

  @Test
  fun emitsSafeNotFoundDetails() {
    @Suppress("UNCHECKED_CAST")
    val repository = mock(CrudRepository::class.java) as CrudRepository<String, Long>
    `when`(repository.findById(42L)).thenReturn(Optional.empty())

    val exception = assertThrows(NotFoundException::class.java) {
      repository.findByIdOrThrow(42L, "Item")
    }

    assertEquals(404, exception.status)
    assertEquals(ErrorCode.NOT_FOUND, exception.code)
    assertEquals("Item not found", exception.publicDetail)
    assertEquals("Item with ID 42 not found", exception.diagnosticDetail)
  }

}

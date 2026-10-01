package io.github.umbrellaleaf5.kotlinbrella.data

import io.github.umbrellaleaf5.kotlinbrella.data.storage.TestItemEntity
import io.github.umbrellaleaf5.kotlinbrella.data.storage.TestItemRepository
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException
import io.github.umbrellaleaf5.kotlinbrella.util.checkFieldNotNullByName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@Tag("integration")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(classes = [JpaTestApplication::class])
@TestPropertySource(properties = [
  "spring.jpa.hibernate.ddl-auto=create-drop",
  "spring.liquibase.enabled=false",
])
class DataJpaIntegrationTest(
  // repositories:
  @param:Autowired private val itemRepository: TestItemRepository,

  // services:
  @param:Autowired private val jdbcTemplate: JdbcTemplate,
) {

  // MARK: Verify lookup against PostgreSQL
  // --------------------------------------------------

  @Test
  fun findsByNonUuidIdAndRejectsMissingRow() {
    val saved = itemRepository.save(TestItemEntity(name = "found"))
    val id = saved.id.checkFieldNotNullByName { "id" }

    assertEquals("found", itemRepository.findByIdOrThrow(id, "Item").name)
    assertThrows(NotFoundException::class.java) {
      itemRepository.findByIdOrThrow(Long.MAX_VALUE, "Item")
    }
  }

  // --------------------------------------------------

  @Test
  fun uniquenessConflictsAreReportedByPostgresql() {
    itemRepository.save(TestItemEntity(name = "unique"))

    assertThrows(DataIntegrityViolationException::class.java) {
      itemRepository.save(TestItemEntity(name = "unique"))
    }
  }

  // --------------------------------------------------

  @Test
  fun foreignKeyConflictsAreReportedByPostgresql() {
    jdbcTemplate.execute(
      "ALTER TABLE test_items ADD CONSTRAINT fk_test_item_parent " +
        "FOREIGN KEY (parent_id) REFERENCES test_parents(id)",
    )

    assertThrows(DataIntegrityViolationException::class.java) {
      itemRepository.save(TestItemEntity(name = "missing parent", parentId = Long.MAX_VALUE))
    }
  }

  // --------------------------------------------------

  @Test
  fun rejectsStaleEntityVersion() {
    val id = itemRepository.save(TestItemEntity(name = "before")).id
      .checkFieldNotNullByName { "id" }
    val first = itemRepository.findByIdOrThrow(id, "Item")
    val stale = itemRepository.findByIdOrThrow(id, "Item")
    first.name = "after"
    itemRepository.save(first)

    stale.name = "stale"

    assertThrows(OptimisticLockingFailureException::class.java) {
      itemRepository.save(stale)
    }
  }

  companion object {

    @Container
    @JvmStatic
    val postgres = PostgreSQLContainer("postgres:16-alpine")
      .withDatabaseName("kotlinbrella")
      .withUsername("test")
      .withPassword("test")

    @DynamicPropertySource
    @JvmStatic
    fun configureDatabase(registry: DynamicPropertyRegistry) {
      registry.add("spring.datasource.url", postgres::getJdbcUrl)
      registry.add("spring.datasource.username", postgres::getUsername)
      registry.add("spring.datasource.password", postgres::getPassword)
    }

  }

}

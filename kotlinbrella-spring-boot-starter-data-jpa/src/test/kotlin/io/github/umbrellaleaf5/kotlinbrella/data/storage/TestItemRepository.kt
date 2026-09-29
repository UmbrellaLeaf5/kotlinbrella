package io.github.umbrellaleaf5.kotlinbrella.data.storage

import org.springframework.data.repository.CrudRepository

interface TestItemRepository : CrudRepository<TestItemEntity, Long>

package io.github.umbrellaleaf5.kotlinbrella.data.storage

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
  name = "test_items",
  uniqueConstraints = [UniqueConstraint(columnNames = ["item_name"])],
)
class TestItemEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null,
  @Column(name = "item_name", nullable = false)
  var name: String = "",
  @Column(name = "parent_id")
  var parentId: Long? = null,
  @Version
  var version: Long? = null,
)

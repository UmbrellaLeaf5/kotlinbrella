package io.github.umbrellaleaf5.kotlinbrella.samples.full.item.storage

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sample_items")
class ItemEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: UUID? = null,
  var ownerId: UUID,
  var name: String,
)

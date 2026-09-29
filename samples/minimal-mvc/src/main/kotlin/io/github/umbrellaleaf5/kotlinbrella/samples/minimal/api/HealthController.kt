package io.github.umbrellaleaf5.kotlinbrella.samples.minimal.api

import io.github.umbrellaleaf5.kotlinbrella.samples.minimal.service.HealthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
class HealthController(
  // services:
  private val healthService: HealthService,
) {

  // MARK: GET /health
  // --------------------------------------------------

  @GetMapping("/health")
  fun status(): String = healthService.status()

}

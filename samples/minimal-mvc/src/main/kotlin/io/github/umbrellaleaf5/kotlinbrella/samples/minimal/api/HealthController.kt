package io.github.umbrellaleaf5.kotlinbrella.samples.minimal.api

import io.github.umbrellaleaf5.kotlinbrella.samples.minimal.service.HealthService
import io.github.umbrellaleaf5.kotlinbrella.samples.minimal.Constants
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

  @GetMapping(Constants.Api.HEALTH_PATH)
  fun status(): String = healthService.status()

}

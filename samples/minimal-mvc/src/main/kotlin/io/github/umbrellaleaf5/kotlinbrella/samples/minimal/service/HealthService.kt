package io.github.umbrellaleaf5.kotlinbrella.samples.minimal.service

import org.springframework.stereotype.Service

@Service
class HealthService {

  // --------------------------------------------------

  fun status(): String = "ok"

}

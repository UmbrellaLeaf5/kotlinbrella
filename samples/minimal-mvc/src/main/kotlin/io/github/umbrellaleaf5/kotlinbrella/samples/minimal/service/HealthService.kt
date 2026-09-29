package io.github.umbrellaleaf5.kotlinbrella.samples.minimal.service

import io.github.umbrellaleaf5.kotlinbrella.samples.minimal.Constants
import org.springframework.stereotype.Service

@Service
class HealthService {

  // --------------------------------------------------

  fun status(): String = Constants.Status.UP

}

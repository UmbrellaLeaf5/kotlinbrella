package io.github.umbrellaleaf5.kotlinbrella.openapi

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenApiFixtureController {

  // --------------------------------------------------

  @GetMapping("/problem")
  fun problem(): String = throw BadRequestException.unified("Invalid input")

}

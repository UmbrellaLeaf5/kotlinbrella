package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.BadRequestException
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class WebFixtureController {

  // MARK: Exercise MVC exception mappings
  // --------------------------------------------------

  @GetMapping("/probe")
  fun probe(@RequestParam("value") valueString: String): String = valueString

  // --------------------------------------------------

  @PostMapping("/probe", consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun postProbe(): String = "ok"

  // --------------------------------------------------

  @GetMapping("/failure")
  fun failure(): String = throw BadRequestException("private", "public")

}

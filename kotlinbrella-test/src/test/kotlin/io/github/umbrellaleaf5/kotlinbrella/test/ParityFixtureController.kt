package io.github.umbrellaleaf5.kotlinbrella.test

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ParityFixtureController {

  // --------------------------------------------------

  @GetMapping("/probe")
  fun probe(): ResponseEntity<Map<String, String>> =
    ResponseEntity.status(404).body(
      mapOf("error" to "Not Found", "message" to "Gone")
    )

}

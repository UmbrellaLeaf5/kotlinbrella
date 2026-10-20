package io.github.umbrellaleaf5.kotlinbrella.test

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ParityFixtureController {

  // --------------------------------------------------

  @GetMapping("/probe")
  fun probe(): ResponseEntity<Map<String, String>> =
    ResponseEntity.status(404).body(
      mapOf("error" to "Not Found", "message" to "Gone")
    )

  // --------------------------------------------------

  @PostMapping("/probe")
  fun postProbe(): ResponseEntity<Map<String, String>> =
    ResponseEntity.status(404).body(
      mapOf("error" to "Not Found", "message" to "Gone")
    )

  // --------------------------------------------------

  @GetMapping("/typed")
  fun typed(): ResponseEntity<Map<String, Int>> =
    ResponseEntity.status(404).body(mapOf("code" to 404))

  // --------------------------------------------------

  @GetMapping("/extra")
  fun extra(): ResponseEntity<Map<String, String>> =
    ResponseEntity.status(404).body(
      mapOf("error" to "Not Found", "message" to "Gone", "extra" to "unexpected")
    )

}

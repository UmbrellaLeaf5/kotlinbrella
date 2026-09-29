package io.github.umbrellaleaf5.kotlinbrella.samples.minimal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MinimalMvcApplication

fun main(args: Array<String>) {
  runApplication<MinimalMvcApplication>(*args)
}

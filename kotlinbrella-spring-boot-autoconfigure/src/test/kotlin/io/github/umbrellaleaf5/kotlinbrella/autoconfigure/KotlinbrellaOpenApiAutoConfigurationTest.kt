package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner

class KotlinbrellaOpenApiAutoConfigurationTest {

  private val context = WebApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(KotlinbrellaOpenApiAutoConfiguration::class.java))

  // MARK: Register canonical problem documentation
  // --------------------------------------------------

  @Test
  fun registersCanonicalProblemDocumentation() {
    context.run { application ->
      assertTrue(application.containsBean("kotlinbrellaErrorOperationCustomizer"))
      assertTrue(application.containsBean("kotlinbrellaSchemaCustomizer"))
    }
  }

}

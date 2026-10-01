package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner

class KotlinbrellaOpenApiAutoConfigurationTest {

  private val context = WebApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(KotlinbrellaOpenApiAutoConfiguration::class.java))

  // MARK: Allow existing API contracts to provide their own error documentation
  // --------------------------------------------------

  @Test
  fun canDisableCanonicalProblemDocumentation() {
    context.run { application ->
      assertTrue(application.containsBean("kotlinbrellaErrorOperationCustomizer"))
    }

    context.withPropertyValues("kotlinbrella.openapi.enabled=false").run { application ->
      assertFalse(application.containsBean("kotlinbrellaErrorOperationCustomizer"))
      assertFalse(application.containsBean("kotlinbrellaSchemaCustomizer"))
    }
  }

}

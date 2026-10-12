package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

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
      assertFalse(application.containsBean("apiErrorCodeValidator"))
    }
  }

  // --------------------------------------------------

  @Test
  fun registersCodeValidatorWithHandlerMappings() {
    context.withBean(RequestMappingHandlerMapping::class.java).run { application ->
      assertTrue(application.containsBean("apiErrorCodeValidator"))
    }
  }

}

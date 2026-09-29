package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner

class KotlinbrellaWebAutoConfigurationTest {

  private val webContext = WebApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(KotlinbrellaWebAutoConfiguration::class.java))

  // MARK: Activate only when Servlet MVC is available
  // --------------------------------------------------

  @Test
  fun registersWebBeansInServletContext() {
    webContext.run { context ->
      assertTrue(context.containsBean("kotlinbrellaErrorAdvice"))
      assertTrue(context.containsBean("kotlinbrellaRequestIdFilter"))
    }
  }

  // --------------------------------------------------

  @Test
  fun canBeDisabledExplicitly() {
    webContext.withPropertyValues("kotlinbrella.web.enabled=false").run { context ->
      assertFalse(context.containsBean("kotlinbrellaErrorAdvice"))
      assertFalse(context.containsBean("kotlinbrellaRequestIdFilter"))
    }
  }

  // --------------------------------------------------

  @Test
  fun doesNotActivateInNonServletApplications() {
    ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(KotlinbrellaWebAutoConfiguration::class.java))
      .run { context ->
        assertEquals(0, context.getBeansOfType(KotlinbrellaErrorAdvice::class.java).size)
      }
  }

}

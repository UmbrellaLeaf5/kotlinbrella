package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.mock.env.MockEnvironment
import org.springframework.mock.web.MockHttpServletRequest

class KotlinbrellaJpaErrorAdviceTest {

  // MARK: Hide constraint and lock diagnostics
  // --------------------------------------------------

  @Test
  fun mapsPersistenceFailuresToSafeConflict() {
    val advice = KotlinbrellaJpaErrorAdvice(
      KotlinbrellaErrorAdvice(
        KotlinbrellaWebProperties(),
        MockEnvironment(),
        ErrorViolationMapper(),
      ),
    )
    val request = MockHttpServletRequest()
    val duplicate = advice.integrityViolation(
      DataIntegrityViolationException("secret_constraint_name"), request)
    val stale = advice.optimisticLock(
      OptimisticLockingFailureException("private row version"), request)

    assertEquals(409, duplicate.statusCode.value())
    assertEquals(409, stale.statusCode.value())
    assertFalse(duplicate.body.toString().contains("secret_constraint_name"))
    assertFalse(stale.body.toString().contains("private row version"))
  }

  // --------------------------------------------------

  @Test
  fun registersPersistenceAdviceOnlyWithWebAdvice() {
    val webContext = WebApplicationContextRunner().withConfiguration(
      AutoConfigurations.of(
        KotlinbrellaWebAutoConfiguration::class.java,
        KotlinbrellaJpaAutoConfiguration::class.java,
      ),
    )

    webContext.run { context ->
      assertTrue(context.containsBean("kotlinbrellaJpaErrorAdvice"))
    }

    webContext.withPropertyValues("kotlinbrella.web.enabled=false").run { context ->
      assertFalse(context.containsBean("kotlinbrellaJpaErrorAdvice"))
    }

    webContext.withPropertyValues("kotlinbrella.data-jpa.errors.enabled=false").run { context ->
      assertFalse(context.containsBean("kotlinbrellaJpaErrorAdvice"))
    }
  }

}

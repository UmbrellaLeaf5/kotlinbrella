package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker
import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision
import io.github.umbrellaleaf5.kotlinbrella.error.ForbiddenException
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class KotlinbrellaAccessAutoConfigurationTest {

  private val context = ApplicationContextRunner()
    .withUserConfiguration(AccessTestConfiguration::class.java)
    .withConfiguration(AutoConfigurations.of(KotlinbrellaAccessAutoConfiguration::class.java))

  // MARK: Apply checker decisions through Spring AOP
  // --------------------------------------------------

  @Test
  fun enforcesAllowedMissingAndConcealedAccess() {
    context.run { application ->
      val fixture = application.getBean(AccessFixture::class.java)
      val checker = application.getBean(MutableAccessChecker::class.java)

      assertEquals("item", fixture.access("user", "item"))
      assertEquals("item", checker.lastResourceId)
      assertEquals("user", checker.lastUserId)

      checker.decision = AccessDecision.NOT_FOUND
      assertThrows(NotFoundException::class.java) { fixture.access("user", "item") }

      checker.decision = AccessDecision.FORBIDDEN
      assertThrows(NotFoundException::class.java) { fixture.access("user", "item") }
      assertThrows(ForbiddenException::class.java) { fixture.explicit("user", "item") }
    }
  }

  // --------------------------------------------------

  @Test
  fun reportsMissingCheckersAndBrokenParameterNames() {
    context.run { application ->
      val fixture = application.getBean(AccessFixture::class.java)

      assertTrue(assertThrows(IllegalStateException::class.java) {
        fixture.missing("user", "item")
      }.message.orEmpty().contains("unknown"))
      assertTrue(assertThrows(IllegalStateException::class.java) {
        fixture.broken("user", "item")
      }.message.orEmpty().contains("wrongName"))
    }
  }

  // --------------------------------------------------

  @Test
  fun acceptsConfiguredArgumentTemplates() {
    context.withPropertyValues(
      "kotlinbrella.access.resource-id-template={resource}KeyString",
      "kotlinbrella.access.user-id-parameter=actorIdString",
    ).run { application ->
      val fixture = application.getBean(AccessFixture::class.java)
      val checker = application.getBean(MutableAccessChecker::class.java)

      assertEquals("item", fixture.templated("user", "item"))
      assertEquals("item", checker.lastResourceId)
      assertEquals("user", checker.lastUserId)
    }
  }

  // --------------------------------------------------

  @Test
  fun documentsProxySelfInvocationLimitation() {
    context.run { application ->
      application.getBean(MutableAccessChecker::class.java).decision = AccessDecision.FORBIDDEN

      assertEquals("item", application.getBean(AccessFixture::class.java)
        .selfInvocation("user", "item"))
    }
  }

  // --------------------------------------------------

  @Test
  fun documentsInterfaceAnnotationPlacement() {
    context.run { application ->
      application.getBean(MutableAccessChecker::class.java).decision = AccessDecision.FORBIDDEN

      assertEquals("item", application.getBean(AccessFixtureImplementation::class.java)
        .access("user", "item"))
    }
  }

  // --------------------------------------------------

  @Test
  fun rejectsDuplicateCheckerKeys() {
    val first = MutableAccessChecker()
    val second = MutableAccessChecker()

    assertThrows(IllegalArgumentException::class.java) {
      OwnershipRegistry(listOf<AccessChecker>(first, second))
    }
  }

  // --------------------------------------------------

  @Test
  fun disablesAspectWhenRequested() {
    context.withPropertyValues("kotlinbrella.access.enabled=false").run { application ->
      assertTrue(application.getBeansOfType(OwnershipAspect::class.java).isEmpty())
    }
  }

  // --------------------------------------------------

  @Test
  fun startsWithoutCheckersUntilOwnershipIsUsed() {
    ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(KotlinbrellaAccessAutoConfiguration::class.java))
      .run { application ->
        assertEquals(null, application.startupFailure)
        assertTrue(application.containsBean("kotlinbrellaOwnershipRegistry"))
      }
  }

}

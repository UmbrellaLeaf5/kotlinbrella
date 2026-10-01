package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.AccessChecker
import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnClass(Aspect::class)
@ConditionalOnProperty(
  prefix = Constants.Configuration.ACCESS_PREFIX,
  name = [Constants.Configuration.ENABLED],
  matchIfMissing = true,
)
@EnableConfigurationProperties(KotlinbrellaAccessProperties::class)
class KotlinbrellaAccessAutoConfiguration {

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaOwnershipRegistry(checkers: List<AccessChecker>): OwnershipRegistry =
    OwnershipRegistry(checkers)

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaOwnershipAspect(
    registry: OwnershipRegistry,
    properties: KotlinbrellaAccessProperties,
  ): OwnershipAspect = OwnershipAspect(registry, properties)

}

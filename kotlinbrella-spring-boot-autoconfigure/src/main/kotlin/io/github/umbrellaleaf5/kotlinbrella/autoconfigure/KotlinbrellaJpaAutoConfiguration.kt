package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.core.PropertyReferenceException

@AutoConfiguration(after = [KotlinbrellaWebAutoConfiguration::class])
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(OptimisticLockingFailureException::class, PropertyReferenceException::class)
@ConditionalOnProperty(
  prefix = Constants.Configuration.DATA_JPA_ERRORS_PREFIX,
  name = [Constants.Configuration.ENABLED],
  matchIfMissing = true,
)
@ConditionalOnBean(KotlinbrellaErrorAdvice::class)
class KotlinbrellaJpaAutoConfiguration {

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaJpaErrorAdvice(
    errorAdvice: KotlinbrellaErrorAdvice,
  ): KotlinbrellaJpaErrorAdvice = KotlinbrellaJpaErrorAdvice(errorAdvice)

}

package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.web.servlet.DispatcherServlet

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(DispatcherServlet::class)
@ConditionalOnProperty(prefix = "kotlinbrella.web", name = ["enabled"], matchIfMissing = true)
@EnableConfigurationProperties(KotlinbrellaWebProperties::class)
class KotlinbrellaWebAutoConfiguration {

  // MARK: Register MVC error and tracing support
  // --------------------------------------------------

  @Bean
  fun kotlinbrellaErrorAdvice(
    properties: KotlinbrellaWebProperties,
    environment: Environment,
  ): KotlinbrellaErrorAdvice = KotlinbrellaErrorAdvice(properties, environment)

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaRequestIdFilter(
    properties: KotlinbrellaWebProperties,
  ): KotlinbrellaRequestIdFilter = KotlinbrellaRequestIdFilter(properties)

}

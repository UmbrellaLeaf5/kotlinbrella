package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnClass(OperationCustomizer::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
  prefix = Constants.Configuration.OPENAPI_PREFIX,
  name = [Constants.Configuration.ENABLED],
  matchIfMissing = true,
)
class KotlinbrellaOpenApiAutoConfiguration {

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaErrorOperationCustomizer(): OperationCustomizer =
    KotlinbrellaErrorOperationCustomizer()

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaSchemaCustomizer(): OpenApiCustomizer = KotlinbrellaSchemaCustomizer()

}

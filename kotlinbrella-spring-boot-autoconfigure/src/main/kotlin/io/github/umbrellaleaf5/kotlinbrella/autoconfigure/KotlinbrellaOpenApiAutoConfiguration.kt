package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnClass(OperationCustomizer::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(KotlinbrellaWebProperties::class)
class KotlinbrellaOpenApiAutoConfiguration {

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaErrorOperationCustomizer(): OperationCustomizer =
    KotlinbrellaErrorOperationCustomizer()

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaSchemaCustomizer(
    properties: KotlinbrellaWebProperties,
  ): OpenApiCustomizer = KotlinbrellaSchemaCustomizer(properties)

}

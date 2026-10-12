package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
@ConditionalOnClass(OperationCustomizer::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(KotlinbrellaWebProperties::class)
class KotlinbrellaOpenApiAutoConfiguration {

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaErrorOperationCustomizer(
    properties: KotlinbrellaWebProperties,
  ): OperationCustomizer = KotlinbrellaErrorOperationCustomizer(properties)

  // --------------------------------------------------

  @Bean
  fun kotlinbrellaSchemaCustomizer(
    properties: KotlinbrellaWebProperties,
  ): OpenApiCustomizer = KotlinbrellaSchemaCustomizer(properties)

  // --------------------------------------------------

  @Bean
  @ConditionalOnBean(RequestMappingHandlerMapping::class)
  fun apiErrorCodeValidator(
    @Qualifier("requestMappingHandlerMapping") mappings: RequestMappingHandlerMapping,
  ): ApiErrorCodeValidator = ApiErrorCodeValidator(mappings)

}

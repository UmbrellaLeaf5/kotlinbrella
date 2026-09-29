package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
class AccessTestConfiguration {

  // --------------------------------------------------

  @Bean
  fun accessFixture(): AccessFixture = AccessFixture()

  // --------------------------------------------------

  @Bean
  fun accessChecker(): MutableAccessChecker = MutableAccessChecker()

  // --------------------------------------------------

  @Bean
  fun accessFixtureImplementation(): AccessFixtureImplementation = AccessFixtureImplementation()

}

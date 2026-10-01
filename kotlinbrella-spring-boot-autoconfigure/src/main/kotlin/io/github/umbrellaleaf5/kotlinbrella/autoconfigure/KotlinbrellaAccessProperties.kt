package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(Constants.Configuration.ACCESS_PREFIX)
class KotlinbrellaAccessProperties {

  var enabled: Boolean = true
  var resourceIdTemplate: String = Constants.Access.RESOURCE_ID_TEMPLATE
  var userIdParameter: String = Constants.Access.USER_ID_PARAMETER

}

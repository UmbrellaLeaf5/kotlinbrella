package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(Constants.Configuration.WEB_PREFIX)
class KotlinbrellaWebProperties {

  var enabled: Boolean = true
  var exposeDebugDetails: Boolean = false
  var diagnosticProfiles: Set<String> = emptySet()
  var requestIdHeader: String = Constants.Web.REQUEST_ID_HEADER
  var logExpected4xx: Boolean = false

}

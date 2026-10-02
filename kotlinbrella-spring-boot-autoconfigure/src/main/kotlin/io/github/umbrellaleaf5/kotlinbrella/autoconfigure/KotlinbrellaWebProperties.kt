package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.Mode
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(Constants.Configuration.WEB_PREFIX)
class KotlinbrellaWebProperties {

  var enabled: Boolean = true
  var mode: Mode = Mode.PROD
  var requestIdHeader: String = Constants.Web.REQUEST_ID_HEADER
  var errorShape: ErrorShape = ErrorShape.STANDARD

}

package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.autoconfigure.enum.ErrorShape
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.springdoc.core.customizers.OpenApiCustomizer

class KotlinbrellaSchemaCustomizer(
  private val properties: KotlinbrellaWebProperties = KotlinbrellaWebProperties(),
) : OpenApiCustomizer {

  // MARK: Register canonical error schemas once
  // --------------------------------------------------

  override fun customise(openApi: OpenAPI) {
    if (properties.errorShape == ErrorShape.SIMPLE) return

    val components = openApi.components ?: Components().also {
      openApi.components = it
    }

    if (components.schemas?.containsKey(Constants.ApiSpec.VIOLATION_SCHEMA) != true)
      components.addSchemas(Constants.ApiSpec.VIOLATION_SCHEMA, violationSchema())

    if (components.schemas?.containsKey(Constants.ApiSpec.PROBLEM_SCHEMA) != true)
      components.addSchemas(Constants.ApiSpec.PROBLEM_SCHEMA, problemSchema())
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun violationSchema(): Schema<*> = ObjectSchema()
    .addProperty(Constants.ApiSpec.FIELD_KEY, StringSchema().nullable(true))
    .addProperty(Constants.ApiSpec.MESSAGE_KEY, StringSchema())
    .addProperty(Constants.Web.CODE_KEY, StringSchema())

  // --------------------------------------------------

  private fun problemSchema(): Schema<*> = ObjectSchema()
    .addProperty(Constants.Web.TYPE_KEY, StringSchema())
    .addProperty(Constants.ApiSpec.TITLE_KEY, StringSchema())
    .addProperty(
      Constants.ApiSpec.STATUS_KEY,
      IntegerSchema().description(Constants.ApiSpec.STATUS_DESCRIPTION),
    )
    .addProperty(Constants.ApiSpec.DETAIL_KEY, StringSchema())
    .addProperty(Constants.ApiSpec.INSTANCE_KEY, StringSchema())
    .addProperty(Constants.Web.CODE_KEY, StringSchema())
    .addProperty(Constants.Web.TRACE_ID_KEY, StringSchema())
    .addProperty(
      Constants.Web.VIOLATIONS_KEY,
      ArraySchema().items(Schema<Any>().`$ref`(Constants.ApiSpec.VIOLATION_REFERENCE)),
    )

}

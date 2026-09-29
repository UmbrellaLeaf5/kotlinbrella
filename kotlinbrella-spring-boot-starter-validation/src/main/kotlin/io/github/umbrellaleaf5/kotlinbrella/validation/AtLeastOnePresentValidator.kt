package io.github.umbrellaleaf5.kotlinbrella.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.beans.Introspector
import java.lang.reflect.Method

class AtLeastOnePresentValidator : ConstraintValidator<AtLeastOnePresent, Any> {

  private lateinit var propertyNames: List<String>

  // MARK: Validate annotation configuration
  // --------------------------------------------------

  override fun initialize(constraintAnnotation: AtLeastOnePresent) {
    propertyNames = constraintAnnotation.properties.toList()
    require(propertyNames.isNotEmpty()) { Constants.Validation.EMPTY_PROPERTY_LIST }
    require(propertyNames.none { it.isBlank() }) { Constants.Validation.BLANK_PROPERTY_NAME }
    require(propertyNames.size == propertyNames.distinct().size) {
      Constants.Validation.DUPLICATE_PROPERTY_NAME
    }
  }

  // MARK: Validate selected patch fields
  // --------------------------------------------------

  override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
    if (value == null) return true

    val accessors = accessors(value.javaClass)

    for (propertyName in propertyNames)
      require(accessors.containsKey(propertyName)) {
        "${Constants.Validation.UNKNOWN_PROPERTY}: $propertyName"
      }

    return propertyNames.any { propertyName ->
      val selectedValue = accessors.getValue(propertyName).invoke(value)
      when (selectedValue) {
        null -> false
        is CharSequence -> selectedValue.isNotBlank()
        is Collection<*> -> selectedValue.isNotEmpty()
        is Map<*, *> -> selectedValue.isNotEmpty()
        is Array<*> -> selectedValue.isNotEmpty()
        else -> true
      }
    }
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun accessors(type: Class<*>): Map<String, Method> {
    val beanAccessors = Introspector.getBeanInfo(type).propertyDescriptors
      .mapNotNull { descriptor ->
        descriptor.readMethod?.let { descriptor.name to it }
      }.toMap()
    val records = type.recordComponents?.associate { it.name to it.accessor } ?: emptyMap()
    val booleanAccessors = type.methods.filter { method ->
      method.name.startsWith("is") && method.parameterCount == 0 &&
        (method.returnType == Boolean::class.javaPrimitiveType ||
          method.returnType == Boolean::class.javaObjectType)
    }.associate { it.name to it }

    return beanAccessors + records + booleanAccessors
  }

}

package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorViolation
import jakarta.validation.ConstraintViolation
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError

class ErrorViolationMapper {

  // MARK: Convert field validation failures
  // --------------------------------------------------

  fun toFieldViolation(error: FieldError): ErrorViolation = ErrorViolation(
    field = error.field,
    message = error.defaultMessage ?: Constants.ErrorDescription.INVALID_VALUE,
    code = ErrorCode.BAD_REQUEST,
  )

  // MARK: Convert global validation failures
  // --------------------------------------------------

  fun toGlobalViolation(error: ObjectError): ErrorViolation = ErrorViolation(
    field = null,
    message = error.defaultMessage ?: Constants.ErrorDescription.INVALID_VALUE,
    code = if (error.code == Constants.Validation.AT_LEAST_ONE_PRESENT_NAME)
      ErrorCode.AT_LEAST_ONE_PRESENT else ErrorCode.BAD_REQUEST,
  )

  // MARK: Convert method constraint failures
  // --------------------------------------------------

  fun toConstraintViolation(violation: ConstraintViolation<*>): ErrorViolation = ErrorViolation(
    field = violation.propertyPath.toString(),
    message = violation.message,
    code = if (violation.constraintDescriptor.annotation.annotationClass.java.name ==
      Constants.Validation.AT_LEAST_ONE_PRESENT_CLASS)
      ErrorCode.AT_LEAST_ONE_PRESENT else ErrorCode.BAD_REQUEST,
  )

  // MARK: Convert violation to explicitly named JSON properties
  // --------------------------------------------------

  fun toProperties(violation: ErrorViolation): Map<String, Any?> = mapOf(
    Constants.ApiSpec.FIELD_KEY to violation.field,
    Constants.ApiSpec.MESSAGE_KEY to violation.message,
    Constants.Web.CODE_KEY to violation.code,
  )

}

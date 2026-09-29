package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.error.ConflictException
import io.github.umbrellaleaf5.kotlinbrella.error.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
class KotlinbrellaJpaErrorAdvice(
  // services:
  private val errorAdvice: KotlinbrellaErrorAdvice,
) {

  // MARK: Render persistence conflicts without exposing database details
  // --------------------------------------------------

  @ExceptionHandler(OptimisticLockingFailureException::class)
  fun optimisticLock(
    exception: OptimisticLockingFailureException,
    request: HttpServletRequest,
  ): ResponseEntity<ProblemDetail> = errorAdvice.apiException(
    ConflictException(
      exception.message ?: Constants.ErrorDescription.CONFLICT,
      Constants.ErrorDescription.CONFLICT,
      ErrorCode.CONFLICT,
      exception,
    ),
    request,
  )

  // --------------------------------------------------

  @ExceptionHandler(DataIntegrityViolationException::class)
  fun integrityViolation(
    exception: DataIntegrityViolationException,
    request: HttpServletRequest,
  ): ResponseEntity<ProblemDetail> = errorAdvice.apiException(
    ConflictException(
      exception.message ?: Constants.ErrorDescription.CONFLICT,
      Constants.ErrorDescription.CONFLICT,
      ErrorCode.CONFLICT,
      exception,
    ),
    request,
  )

}

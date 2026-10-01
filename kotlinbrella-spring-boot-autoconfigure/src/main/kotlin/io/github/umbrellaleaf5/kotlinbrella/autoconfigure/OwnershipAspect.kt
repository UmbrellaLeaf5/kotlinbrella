package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import io.github.umbrellaleaf5.kotlinbrella.access.AccessDecision
import io.github.umbrellaleaf5.kotlinbrella.access.CheckOwnership
import io.github.umbrellaleaf5.kotlinbrella.access.DenialPolicy
import io.github.umbrellaleaf5.kotlinbrella.error.ForbiddenException
import io.github.umbrellaleaf5.kotlinbrella.error.NotFoundException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.core.DefaultParameterNameDiscoverer

@Aspect
class OwnershipAspect(
  // services:
  private val registry: OwnershipRegistry,

  // params:
  private val properties: KotlinbrellaAccessProperties,
) {

  private val logger = LoggerFactory.getLogger(javaClass)
  private val parameterNames = DefaultParameterNameDiscoverer()

  // MARK: Check application-provided ownership decisions
  // --------------------------------------------------

  /**
   * Получает необработанные идентификаторы из аргументов метода и передаёт их проверяющему.
   * При отказе применяет выбранную политику сокрытия существования ресурса.
   */
  @Around(Constants.Access.OWNERSHIP_POINTCUT)
  fun checkOwnership(
    joinPoint: ProceedingJoinPoint,
    checkOwnership: CheckOwnership,
  ): Any? {
    val signature = joinPoint.signature as MethodSignature
    val names = parameterNames.getParameterNames(signature.method)
      ?: signature.parameterNames
    val resourceParam = checkOwnership.resourceIdParam.ifBlank {
      properties.resourceIdTemplate.replace(
        Constants.Access.RESOURCE_PLACEHOLDER,
        checkOwnership.resource,
      )
    }
    val userParam = checkOwnership.userIdParam.ifBlank { properties.userIdParameter }
    val resourceId = argument(joinPoint.args, names, resourceParam)
    val userId = argument(joinPoint.args, names, userParam)
    val decision = registry.checker(checkOwnership.resource).check(resourceId, userId)

    logger.debug(Constants.Access.DECISION_LOG, checkOwnership.resource, decision)

    return when (decision) {
      AccessDecision.ALLOWED -> joinPoint.proceed()
      AccessDecision.NOT_FOUND -> throw NotFoundException.unified(
        Constants.ErrorDescription.RESOURCE_NOT_FOUND,
      )
      AccessDecision.FORBIDDEN -> when (checkOwnership.policy) {
        DenialPolicy.HIDE_EXISTENCE -> throw NotFoundException.unified(
          Constants.ErrorDescription.RESOURCE_NOT_FOUND,
        )
        DenialPolicy.FORBIDDEN -> throw ForbiddenException.unified(Constants.Access.ACCESS_DENIED)
      }
    }
  }

  // MARK: Private Helpers
  // --------------------------------------------------

  private fun argument(
    arguments: Array<Any?>,
    names: Array<out String?>?,
    name: String,
  ): String {
    val index = names?.indexOf(name) ?: -1

    if (index < 0 || index >= arguments.size)
      throw IllegalStateException("${Constants.Access.MISSING_PARAMETER}: $name")

    return arguments[index] as? String
      ?: throw IllegalStateException("${Constants.Access.MISSING_PARAMETER}: $name")
  }

}

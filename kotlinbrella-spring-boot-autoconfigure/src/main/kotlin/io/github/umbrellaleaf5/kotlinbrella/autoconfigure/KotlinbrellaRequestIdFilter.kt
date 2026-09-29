package io.github.umbrellaleaf5.kotlinbrella.autoconfigure

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

class KotlinbrellaRequestIdFilter(
  private val properties: KotlinbrellaWebProperties,
) : OncePerRequestFilter() {

  // MARK: Propagate a safe request identifier
  // --------------------------------------------------

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    val supplied = request.getHeader(properties.requestIdHeader)
    val requestId = supplied?.takeIf { it.matches(Regex(Constants.Web.REQUEST_ID_PATTERN)) }
      ?: UUID.randomUUID().toString()
    val previousTraceId = MDC.get(Constants.Web.TRACE_ID_KEY)

    request.setAttribute(Constants.Web.TRACE_ID_ATTRIBUTE, requestId)
    response.setHeader(properties.requestIdHeader, requestId)
    MDC.put(Constants.Web.TRACE_ID_KEY, requestId)

    try {
      filterChain.doFilter(request, response)
    }

    finally {
      if (previousTraceId == null)
        MDC.remove(Constants.Web.TRACE_ID_KEY)

      else
        MDC.put(Constants.Web.TRACE_ID_KEY, previousTraceId)
    }
  }

}

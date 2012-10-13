package shybyte.rsgae

import java.io.IOException
import javax.servlet.http._
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import shybyte.rsgae.dao.AuthTokenDao
import shybyte.rsgae.model.AuthToken

@SuppressWarnings(Array("serial"))
class AuthFilter extends Filter {

  override def init(config: FilterConfig) {
  }

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpResponse = response.asInstanceOf[HttpServletResponse]
    val httpRequest = request.asInstanceOf[HttpServletRequest]

    if (isValid(httpRequest, httpResponse)) {
      chain.doFilter(request, response)
    } else {
      httpResponse.setStatus(401)
    }

  }

  def isValid(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): Boolean = {
    if (httpRequest.getMethod().equals("OPTIONS") ||
      AuthToken.isValidPublicPathRequest(httpRequest.getPathInfo(), httpRequest.getMethod())) {
      return true;
    }

    val authorizationHeader = httpRequest.getHeader("Authorization")
    if (authorizationHeader == null) {
      return false;
    }

    val authTokenID = authorizationHeader.substring("Bearer ".length())
    val authToken = AuthTokenDao.getAuthToken(authTokenID)
    return (authToken != null && authToken.isValidForPath(httpRequest.getPathInfo(), httpRequest.getMethod()))
  }

  override def destroy() {

  }

}
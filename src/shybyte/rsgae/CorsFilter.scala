package shybyte.rsgae

import java.io.IOException
import javax.servlet.http._
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

@SuppressWarnings(Array("serial"))
class CorsFilter extends Filter {

  override def init(config: FilterConfig) {
  }

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpResponse = response.asInstanceOf[HttpServletResponse]
    val origin = request.asInstanceOf[HttpServletRequest].getHeader("origin")
    httpResponse.setHeader("access-control-allow-origin", Some(origin).getOrElse("*"))
    httpResponse.setHeader("access-control-allow-headers", "content-type, authorization, origin")
    httpResponse.setHeader("access-control-allow-methods", "GET, PUT, DELETE")
    chain.doFilter(request, response);
  }

  override def destroy() {

  }

}
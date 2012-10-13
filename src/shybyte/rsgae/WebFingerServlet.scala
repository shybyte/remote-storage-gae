package shybyte.rsgae

import java.io.IOException
import javax.servlet.http._

@SuppressWarnings(Array("serial"))
class WebFingerServlet extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val port = req.getServerPort()
    val userAddress =  req.getParameter("resource").substring("acct:".length)
    val userName = userAddress.split('@').first
    resp.setContentType("application/json")
    resp.getWriter().println(
      """{"links":[{
	    "href":"http://local.dev:""" + port + """/storage/"""+userName+"""",
	    "rel":"remoteStorage",
	    "type":"https://www.w3.org/community/rww/wiki/read-write-web-00#simple",
	    "properties":{"auth-method":"https://tools.ietf.org/html/draft-ietf-oauth-v2-26#section-4.2",
	    "auth-endpoint":"http://local.dev:""" + port + """/auth/"""+userName+""""}}]}""")
  }

}
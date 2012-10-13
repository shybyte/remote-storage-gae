package shybyte.rsgae

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.io.IOException
import javax.servlet.http._
import shybyte.rsgae.model.AuthToken
import shybyte.rsgae.dao.AuthTokenDao

@SuppressWarnings(Array("serial"))
class AuthMeServlet extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val username = req.getPathInfo().substring(1)
    val scopes = req.getParameter("scope").split(" ")
    val clientID = req.getParameter("client_id")
    
    val authToken = AuthToken.generateAuthToken(username,scopes.toList)
    AuthTokenDao.save(authToken)
    
    var url = req.getParameter("redirect_uri")+"#access_token="+authToken.getId()
    val page =
      <html>
        <head>
          <title>Allow Remote Storage Access</title>
        </head>
        <body>
          <h1>Allow Remote Storage Access?</h1>
    	  The web app <strong>{clientID}</strong> requested the following rights:
          <ul>{for (scope <- scopes) yield <li>{scope}</li>}</ul>
    	  <p><a href={url}>Allow</a></p>
        </body>
      </html>;
    resp.getWriter().println(page)
  }

}
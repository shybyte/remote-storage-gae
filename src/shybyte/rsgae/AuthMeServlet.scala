package shybyte.rsgae

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.io.IOException
import javax.servlet.http._
import shybyte.rsgae.model.AuthToken
import shybyte.rsgae.dao.AuthTokenDao
import com.google.appengine.api.users.UserServiceFactory
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService

@SuppressWarnings(Array("serial"))
class AuthMeServlet extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val userService = UserServiceFactory.getUserService()
    val user = userService.getCurrentUser()

    if (user == null) {
      resp.sendRedirect(userService.createLoginURL(getCompleteURI(req)))
      return ;
    }

    val username = req.getPathInfo().substring(1)
    val loggedInUserName = user.getEmail().split("@").first
    if (username == loggedInUserName) {
      showConfirmationPage(req, resp, username)
    } else {
      showWrongUsernameWarningPage(req, resp, username, userService, loggedInUserName);
    }

  }
  
  def getCompleteURI(req: HttpServletRequest) : String = {
	  val queryTail = if (req.getQueryString() == null) "" else "?"+req.getQueryString()
	  return req.getRequestURI() + queryTail
  } 

  private def showConfirmationPage(req: javax.servlet.http.HttpServletRequest, resp: javax.servlet.http.HttpServletResponse, username: java.lang.String): Unit = {
    val scopes = req.getParameter("scope").split(" ")
    val clientID = req.getParameter("client_id")

    val authToken = AuthToken.generateAuthToken(username, scopes.toList)
    AuthTokenDao.save(authToken)

    var url = req.getParameter("redirect_uri") + "#access_token=" + authToken.getId()
    val page =
      <html>
        <head>
          <title>Allow Remote Storage Access</title>
        </head>
        <body>
          <h1>Allow Remote Storage Access?</h1>
          The web app <strong>{ clientID }</strong>
          requested the following rights:
          <ul>{ for (scope <- scopes) yield <li>{ scope }</li> }</ul>
          <p><a href={ url }>Allow</a></p>
        </body>
      </html>;
    resp.getWriter().println(page)
  }

  private def showWrongUsernameWarningPage(req: javax.servlet.http.HttpServletRequest,
    resp: javax.servlet.http.HttpServletResponse,
    username: String, userService: UserService, loggedInUserName: String): Unit = {
    val page =
      <html>
        <head>
          <title>Wrong Username</title>
        </head>
        <body>
          <h1>Wrong Username?</h1>
          Your remotestorage user name <strong>{ username }</strong>
          does not match your google id <strong>{ loggedInUserName}</strong>
          !
          <p>Please try again:<a href={ userService.createLogoutURL(userService.createLoginURL(getCompleteURI(req))) }>Relogin</a></p>
        </body>
      </html>;
    resp.getWriter().println(page)
  }

}
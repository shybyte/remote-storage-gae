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
import shybyte.rsgae.model.Scope

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

  def getCompleteURI(req: HttpServletRequest): String = {
    val queryTail = if (req.getQueryString() == null) "" else "?" + req.getQueryString()
    return req.getRequestURI() + queryTail
  }

  def humanizeScope(scopeString: String):String = {
    val scope = Scope(scopeString)
    val optionLabel = scope.options match {
      case "rw" => "(Read and Write)"
      case "r" => "(Read)"
    }
    val pathLabel = if (scope.path == "") "All Remote Storage Data" else scope.path
    return pathLabel+" "+optionLabel
  }

  private def showConfirmationPage(req: javax.servlet.http.HttpServletRequest, resp: javax.servlet.http.HttpServletResponse, username: java.lang.String): Unit = {
    val scopes = req.getParameter("scope").split(" ")
    val clientID = req.getParameter("client_id")

    val authToken = AuthToken.generateAuthToken(username, scopes.toList)
    AuthTokenDao.save(authToken)

    var url = req.getParameter("redirect_uri") + "#access_token=" + authToken.getId()

    val pageContent =
      <div>
        A web app from server&nbsp;<strong>{ clientID }</strong>
        requested the following rights:
        <ul>{ for (scope <- scopes) yield <li>{ humanizeScope(scope) }</li> }</ul>
        <p><a href={ url } class="btn btn-primary">Allow</a></p>
      </div>

    val jspPage = getServletContext().getRequestDispatcher("/WEB-INF/jsp/allow.jsp")
    req.setAttribute("title", "Allow Remote Storage Access?");
    req.setAttribute("pageContent", pageContent.toString());
    jspPage.forward(req, resp);
  }

  private def showWrongUsernameWarningPage(req: javax.servlet.http.HttpServletRequest,
    resp: javax.servlet.http.HttpServletResponse,
    username: String, userService: UserService, loggedInUserName: String): Unit = {
    val orignalRedirectUrl = req.getParameter("redirect_uri")
    val potentielRemoteStorageUsreAddress = loggedInUserName+"@"+req.getServerName()
    val reloginWithDifferentGoogeAccountUrl = userService.createLogoutURL(userService.createLoginURL(getCompleteURI(req)))
    
    val pageContent =
      <div>
        Your remotestorage user&nbsp;name&nbsp;<strong>{ username }</strong>
        does not match your google&nbsp;id&nbsp;<strong>{ loggedInUserName }</strong>
        !<br/>
        Please try:
        <ul>
          <li><a href={ reloginWithDifferentGoogeAccountUrl }>Relogin with google&nbsp;account&nbsp;<strong>{ username }</strong></a></li>
          <li><a href={ orignalRedirectUrl }>Relogin with remote&nbsp;storage&nbsp;address&nbsp;<strong>{ potentielRemoteStorageUsreAddress }</strong></a></li>
        </ul>
      </div>

    val jspPage = getServletContext().getRequestDispatcher("/WEB-INF/jsp/allow.jsp")
    req.setAttribute("title", "Wrong User Name?");
    req.setAttribute("pageContent", pageContent.toString());
    jspPage.forward(req, resp);
  }

}
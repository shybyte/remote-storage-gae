package shybyte.rsgae

import java.io.IOException
import javax.servlet.http._

@SuppressWarnings(Array("serial"))
class AuthMeServlet extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    var url = req.getParameter("redirect_uri")+"#access_token=123"
    val page =
      <html>
        <head>
          <title>Allow Access</title>
        </head>
        <body>
          <h1>Allow Access?</h1>
          <p><a href={url}>Allow</a></p>
        </body>
      </html>;
    resp.getWriter().println(page)
  }

}
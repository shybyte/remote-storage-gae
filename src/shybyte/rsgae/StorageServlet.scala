package shybyte.rsgae

import java.io.IOException
import javax.servlet.http._
import scala.io.Source
import java.util.logging.Logger
import shybyte.rsgae.model.Resource
import shybyte.rsgae.model.Resource._
import shybyte.rsgae.dao.Dao
import shybyte.rsgae.dao.Dao._

@SuppressWarnings(Array("serial"))
class StorageServlet extends HttpServlet {

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse) {
    val content = Source.fromInputStream(req.getInputStream()).mkString
    save(resource(req.getPathInfo(), content, req.getContentType()))
    resp.getWriter().println("null")
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val path = req.getPathInfo()
    if (path.endsWith("/")) {
      listDirectory(path, resp)
    } else {
      returnResource(path, resp)
    }
  }

  def listDirectory(path: String, resp: HttpServletResponse) {
    resp.setContentType("application/json")
    resp.getWriter().print('{' + Dao.listDirectory(path).map(p => '"' + p._1 + "\":" + p._2).mkString(",") + '}')
  }

  def returnResource(path: String, resp: HttpServletResponse) {
    val resource = getResource(path)
    if (resource == null) {
      resp.setStatus(404);
      return ;
    }
    resp.setContentType(resource.getContentType())
    resp.setDateHeader("last-modified", resource.getTimestamp())
    resp.getWriter().println(resource.getContent())
  }

  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
    deleteResource(req.getPathInfo())
    resp.getWriter().println("null")
  }

}
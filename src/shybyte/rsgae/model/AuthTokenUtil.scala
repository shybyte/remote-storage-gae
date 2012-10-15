package shybyte.rsgae.model

import scala.collection.JavaConversions._

object AuthTokenUtil {
  def generateAuthToken(username: String, scopes: Seq[String]) = AuthToken.generateAuthToken(username, scopes)
}
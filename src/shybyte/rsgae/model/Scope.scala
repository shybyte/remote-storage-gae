package shybyte.rsgae.model

/**
 * @param options "r" or "rw"
 */
case class Scope(path: String, options: String) {
  
  def allowsHttpMethod(httpMethod: String) = httpMethod match {
    case "PUT" => options == "rw"
    case "DELETE" => options == "rw"
    case _ => true
  }
  
}

object Scope {
  val PATTERN = "(.*):(rw?)".r

  def apply(scopeString: String): Scope = {
    val PATTERN(path, rw) = scopeString
    return Scope(path, rw)
  }
}

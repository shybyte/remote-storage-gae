package shybyte.rsgae.dao

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService.ofy
import com.googlecode.objectify.ObjectifyService
import shybyte.rsgae.model.AuthToken

object AuthTokenDao {
  ObjectifyService.register(classOf[AuthToken])

  def save(authtoken: AuthToken) {
    ofy.save().entity(authtoken)
  }

  def getAuthToken(id: String) = ofy().load().`type`(classOf[AuthToken]).id(id).get()

}
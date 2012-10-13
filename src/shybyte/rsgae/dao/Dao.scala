package shybyte.rsgae.dao

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService.ofy
import shybyte.rsgae.model.Resource
import shybyte.rsgae.model.Resource._
import com.googlecode.objectify.ObjectifyService

object Dao {
  ObjectifyService.register(classOf[Resource])

  def save(entity: Resource) {
    ofy.save().entity(entity)
    ensureParentDirectories(entity.getDir());
  }
  
  def ensureParentDirectories(path:String) {
    var currentPath = path;
    while (currentPath.length()>1) {
      ofy.save().entity(directory(currentPath))
      currentPath = getParentDirectory(currentPath)
    }
  }

  def getResource(id: String) = ofy().load().`type`(classOf[Resource]).id(id).get()
  
  def listDirectory(directory: String) :Seq[(String,Long)] = {
    val resources :Seq[Resource] = ofy().load().`type`(classOf[Resource]).filter("dir",directory).list()
    return resources.map(r => (r.getName(),r.getTimestamp()))
  } 

  def deleteResource(id: String) {
    ofy().delete().`type`(classOf[Resource]).id(id)
  }

}
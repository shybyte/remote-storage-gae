package shybyte.rsgae.dao

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.googlecode.objectify.Objectify
import com.googlecode.objectify.ObjectifyService.ofy
import shybyte.rsgae.model.Resource
import shybyte.rsgae.model.Resource._
import com.googlecode.objectify.ObjectifyService
import net.sf.jsr107cache.CacheManager
import java.util.Collections

object Dao {
  ObjectifyService.register(classOf[Resource])
  val  cacheFactory = CacheManager.getInstance().getCacheFactory();
  val  cache = cacheFactory.createCache(Collections.emptyMap());

  def save(entity: Resource) {
    ofy.save().entity(entity)
    ensureUpdatedParentDirectories(entity.getDir());
  }
  
  def ensureUpdatedParentDirectories(path:String) {
    var currentPath = path;
    while (currentPath.length()>1) {
      ofy.save().entity(directory(currentPath))
      cache.remove(dirListingKey(currentPath))
      currentPath = getParentDirectory(currentPath)
    }
  }

  def getResource(id: String) = ofy().load().`type`(classOf[Resource]).id(id).get()
  
  def listDirectory(directory: String) :Seq[(String,Long)] = {
    val cachedDirListing = cache.get(dirListingKey(directory)).asInstanceOf[Seq[(String,Long)]]
    if (cachedDirListing!=null) {
      return cachedDirListing
    }
    val resources :Seq[Resource] = ofy().load().`type`(classOf[Resource]).filter("dir",directory).list()
    val timeStampByPath = resources.filter(_ != null).map(r => (r.getName(),r.getTimestamp()))
    cache.put(dirListingKey(directory),timeStampByPath)
    return timeStampByPath
  } 
  
  def dirListingKey(path:String) = "dir:"+path

  def deleteResource(id: String) {
    val resource = getResource(id)
    if (resource != null) {
    	ofy().delete().`type`(classOf[Resource]).id(id)
    	ensureUpdatedParentDirectories(resource.getDir())
    }
  }

}
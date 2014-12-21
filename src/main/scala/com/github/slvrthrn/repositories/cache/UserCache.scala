package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.repositories.impl.UserRepoImpl
import com.mongodb.casbah.commons.MongoDBObject
import com.twitter.util.Future
import org.bson.types.ObjectId
import scaldi.Injector

/**
 * Created by slvr on 12/16/14.
 */
class UserCache(implicit inj: Injector) extends UserRepoImpl with Cache {

  private val userCache = cacheFor[Option[User]]("users")

  override def findById(id: ObjectId): Future[Option[User]] = {
    userCache.getOrElseUpdate(id) {
      super.findById(id)
    }
  }

  override def findOne(filter: MongoDBObject): Future[Option[User]] = {
    userCache.getOrElseUpdate(filter) {
      super.findOne(filter)
    }
  }

  override def save(entity: User): Future[User] = {
    super.save(entity) onSuccess {
      case u: User => userCache.save(entity._id, Some(u))
    }
  }

}

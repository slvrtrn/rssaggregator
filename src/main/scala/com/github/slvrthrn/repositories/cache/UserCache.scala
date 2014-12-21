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

  private val userLoginCache = cacheFor[Option[User]]("users_login")

  private val userIdCache = cacheFor[Option[User]]("users_id")

//  override def findById(id: ObjectId): Future[Option[User]] = {
//    userCache.findOneOrElseUpdate(id) {
//      super.findById(id)
//    }
//  }
//
//  override def findOne(filter: MongoDBObject): Future[Option[User]] = {
//    userCache.findOneOrElseUpdate(filter) {
//      super.findOne(filter)
//    }
//  }
//
//  override def save(entity: User): Future[User] = {
//    super.save(entity) onSuccess {
//      case user: User => userCache.save(entity._id, user)
//    }
//  }

//  override def saveTraversable(seq: Seq[User]): Future[Seq[User]] = {
//    super.saveTraversable(seq) onSuccess {
//      case seq: Seq => userCache.saveTraversable()
//    }
//  }

  override def findById(id: ObjectId): Future[Option[User]] = {
    userIdCache.getOrElseUpdate(id) {
      super.findById(id)
    }
  }

  override def findByLogin(login: String): Future[Option[User]] = {
    userLoginCache.getOrElseUpdate(login) {
      super.findByLogin(login)
    }
  }

  override def save(entity: User): Future[User] = {
    super.save(entity) onSuccess {
      case user: User =>
        userIdCache.evict(entity._id)
        userLoginCache.save(entity.login, Some(user))
    }
  }

}

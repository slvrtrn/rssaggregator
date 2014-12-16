package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.User
import com.github.slvrthrn.repositories.impl.UserRepoImpl
import com.twitter.util.Future
import org.bson.types.ObjectId
import scaldi.Injector
import com.github.slvrthrn.utils.Twitter._

/**
 * Created by slvr on 12/16/14.
 */
class UserCache(implicit inj: Injector) extends UserRepoImpl with Cache {

//  private val client: RedisClient = inject[RedisClient]
//
//  private implicit val timeout = Timeout(Duration(5, TimeUnit.SECONDS))
//
//  override def findById(id: ObjectId): Future[Option[User]] = {
//    client.get[String](id.toString).asTwitter flatMap {
//      case Some(s: String) => Future value Some(grater[User].fromJSON(s))
//      case _ => super.findById(id)
//    }
//  }

  private val userCache = cacheFor[Option[User]]("users")

  override def findById(id: ObjectId): Future[Option[User]] = {
    userCache.getOrElseUpdate(id) {
      super.findById(id)
    }
  }

  override def save(entity: User): Future[User] = {
    super.save(entity) onSuccess {
      case u: User => userCache.save(entity._id, Some(u))
    }
  }

}

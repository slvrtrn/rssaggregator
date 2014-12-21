package com.github.slvrthrn.repositories

import com.github.slvrthrn.models.entities.User
import com.novus.salat.dao.SalatDAO
import com.twitter.util.Future
import org.bson.types.ObjectId
import com.github.slvrthrn.utils.InjectHelper
import com.novus.salat.global._

/**
 * Created by slvr on 12/6/14.
 */
trait UserRepo extends MongoDaoRepository[User] { self: InjectHelper =>

  protected val collection = db("users")

  protected val dao = new SalatDAO[User, ObjectId](collection = collection) {}

  def findByLogin(login: String): Future[Option[User]]

}

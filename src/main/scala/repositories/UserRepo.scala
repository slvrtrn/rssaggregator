package ru.slvr.repositories

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import ru.slvr.models.User
import ru.slvr.utils.InjectHelper
import com.novus.salat.global._

/**
 * Created by slvr on 12/6/14.
 */
trait UserRepo extends MongoDaoRepository[User] { self: InjectHelper =>
  protected val collection = db("users")
  protected val dao = new SalatDAO[User, ObjectId](collection = collection) {}
}

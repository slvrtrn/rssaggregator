package ru.slvr.services

import com.twitter.util.Future
import ru.slvr.models.User

/**
 * Created by slvr on 12/6/14.
 */
trait UserService {

  def getAllUsers: Future[Seq[User]]
}

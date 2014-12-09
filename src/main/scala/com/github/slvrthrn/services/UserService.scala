package com.github.slvrthrn.services

import com.twitter.util.Future
import com.github.slvrthrn.models.User

/**
 * Created by slvr on 12/6/14.
 */
trait UserService {

  def getAllUsers: Future[Seq[User]]

  def createUser(login: String, email: String, password: String): Future[Option[User]]
}

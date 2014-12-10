package com.github.slvrthrn.services

import com.github.slvrthrn.views.forms.RegForm
import com.twitter.util.Future
import com.github.slvrthrn.models.User

/**
 * Created by slvr on 12/6/14.
 */
trait UserService {

  def checkPassword(password: String, user: User): Boolean

  def checkUserExistence(login: String): Future[Boolean]

  def createUser(form: RegForm): Future[Option[User]]

}

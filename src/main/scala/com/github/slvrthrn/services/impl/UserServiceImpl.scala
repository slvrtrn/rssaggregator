package com.github.slvrthrn.services.impl

import com.github.slvrthrn.services.UserService
import com.github.slvrthrn.utils.InjectHelper
import com.github.slvrthrn.views.forms.{LoginForm, RegForm}
import com.twitter.util.Future
import com.github.slvrthrn.models.User
import com.github.slvrthrn.repositories.UserRepo
import scaldi.Injector
import com.github.t3hnar.bcrypt._


/**
 * Created by slvr on 12/6/14.
 */
class UserServiceImpl(implicit val inj: Injector) extends UserService with InjectHelper {

  val userRepo = inject[UserRepo]

  def checkPassword(password: String, user: User): Boolean = {
    password.isBcrypted(user.password)
  }

  def checkUserExistence(login: String): Future[Boolean] = {
    userRepo.findUser(login) map {
      case Some(user: User) => true
      case _ => false
    }
  }

  def createUser(form: RegForm): Future[Option[User]] = {
    for {
      exist <- checkUserExistence(form.login)
      regResult <- {
        if(!exist) userRepo.save(User(form.login, form.password.bcrypt)).map(Some(_))
        else Future value None
      }
    } yield regResult
  }

  def checkLogin(form: LoginForm): Future[Option[User]] = {
    val result = userRepo.findUser(form.login) map {
      case Some(u: User) =>
        checkPassword(form.password, u) match {
          case true => Some(u)
          case false => None
        }
      case _ => None
    }
    result
  }
}
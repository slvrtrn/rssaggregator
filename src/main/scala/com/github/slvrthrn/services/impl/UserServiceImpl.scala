package com.github.slvrthrn.services.impl

import com.github.slvrthrn.services.UserService
import com.twitter.util.Future
import org.bson.types.ObjectId
import com.github.slvrthrn.models.User
import com.github.slvrthrn.repositories.UserRepo
import scaldi.{Injectable, Injector}
import com.github.t3hnar.bcrypt._


/**
 * Created by slvr on 12/6/14.
 */
class UserServiceImpl(implicit val inj: Injector) extends UserService with Injectable {
  val userRepo = inject[UserRepo]

  def getAllUsers: Future[Seq[User]] = {
    userRepo.find
  }

  def checkPassword(password: String, user: User) = {
    password.isBcrypted(user.password)
  }

  def checkUserExistence(login: String, email: String): Future[Boolean] = {
    userRepo.findUser(login, email) map {
      case Some(user: User) => true
      case _ => false
    }
  }

  def createUser(login: String, email: String, password: String): Future[Option[User]] = {
    for {
      exist <- checkUserExistence(login, email)
      regResult <- if(!exist) userRepo.insertUser(login, email, password.bcrypt).map(Some(_)) else Future None
    } yield regResult
  }
}
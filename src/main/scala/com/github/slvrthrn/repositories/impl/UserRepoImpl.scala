package com.github.slvrthrn.repositories.impl

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import com.twitter.util.Future
import com.github.slvrthrn.models.User
import com.github.slvrthrn.repositories.UserRepo
import com.github.slvrthrn.utils.InjectHelper
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class UserRepoImpl(implicit val inj: Injector) extends UserRepo with InjectHelper{
  def findUserByLogin(login: String): Future[Option[User]] = {
    val filter = MongoDBObject("login" -> login)
    findOne(filter)
  }

  def findUserByEmail(email: String): Future[Option[User]] = {
    val filter = MongoDBObject("email" -> email)
    findOne(filter)
  }

  def findUser(login: String, email: String): Future[Option[User]] = {
    val filter = MongoDBObject(List("login" -> login, "email" -> email))
    findOne(filter)
  }

  def insertUser(login: String, email: String, password: String): Future[User] = {
    val user = User(new ObjectId(), login, email, password)
    save(user)
  }
}

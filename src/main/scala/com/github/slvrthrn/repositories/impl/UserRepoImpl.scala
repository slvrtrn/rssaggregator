package com.github.slvrthrn.repositories.impl

import com.github.slvrthrn.models.entities.User
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import com.twitter.util.Future
import com.github.slvrthrn.repositories.UserRepo
import com.github.slvrthrn.utils.InjectHelper
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class UserRepoImpl(implicit val inj: Injector) extends UserRepo with InjectHelper{

  def findByLogin(login: String): Future[Option[User]] = findOne("login" $eq login)

}

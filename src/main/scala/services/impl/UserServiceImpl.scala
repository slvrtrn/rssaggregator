package ru.slvr.services.impl

import com.twitter.util.Future
import ru.slvr.models.User
import ru.slvr.repositories.UserRepo
import ru.slvr.services.UserService
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class UserServiceImpl(implicit val inj: Injector) extends UserService with Injectable {
  val userRepo = inject[UserRepo]

  def getAllUsers: Future[Seq[User]] = {
    userRepo.find
  }
}
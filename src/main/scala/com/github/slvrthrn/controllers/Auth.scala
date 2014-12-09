package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.User
import com.github.slvrthrn.services.UserService
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class Auth(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]

  post("/reg") { request =>

    val futureReg = userService.createUser(
      request.getParam("login"),
      request.getParam("password")
    )

    futureReg map {
      case Some(user: User) => render.plain("User creation: success")
      case _ => render.plain("User creation: fail")
    }
  }

}
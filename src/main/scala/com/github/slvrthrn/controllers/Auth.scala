package com.github.slvrthrn.controllers

import com.wix.accord._
import com.github.slvrthrn.models.User
import com.github.slvrthrn.services.UserService
import com.github.slvrthrn.views.forms.RegForm
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class Auth(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]

  post("/reg") { request =>

    val regForm = RegForm(request.getParam("login"), request.getParam("password"))

    val validationResult = validate(regForm)

    validationResult match {
      case Success =>
        val futureReg = userService.createUser(regForm)
        futureReg map {
          case Some(user: User) => render.plain("User creation: success")
          case _ => render.plain("User creation: fail")
        }
      case _ => render.plain("Form validation fail").toFuture
    }

  }

}
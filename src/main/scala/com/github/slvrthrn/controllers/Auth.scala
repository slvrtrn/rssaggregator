package com.github.slvrthrn.controllers

import com.github.slvrthrn.views.{LoginView, RegView}
import com.twitter.util.Future
import com.wix.accord._
import com.github.slvrthrn.models.User
import com.github.slvrthrn.services.{SessionService, UserService}
import com.github.slvrthrn.models.forms.{LoginForm, RegForm}
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class Auth(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]
  val sessionService = inject[SessionService]

  get("/login") { request =>

    val loginView = new LoginView
    render.html(loginView.renderHtml).toFuture

  }

  post("/login") { request =>

    val loginForm = LoginForm(request.getParam("login"), request.getParam("password"))

    validate(loginForm) match {
      case Success =>
        userService.checkLogin(loginForm) flatMap {
          case Some(u: User) =>
            sessionService.createSession(u) map {
              case Some(uuid: String) => redirect("/", "Login is OK and session is created").cookie("sid", uuid)
              case _ => render.plain("Database error")
            }
          case None => Future value render.plain("Login failed - incorrect login or password")
        }
      case _ => render.plain("Form validation failed").toFuture
    }

  }

  get("/reg") { request =>

    val regView = new RegView
    render.html(regView.renderHtml).toFuture

  }

  post("/reg") { request =>

    val regForm = RegForm(request.getParam("login"), request.getParam("password"))

    validate(regForm) match {
      case Success =>
        userService.createUser(regForm) map {
          case Some(user: User) => render.plain("User creation: success")
          case _ => render.plain("User creation: fail")
        }
      case _ => render.plain("Form validation fail").toFuture
    }

  }

}
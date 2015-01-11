package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.views.AuthView
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.Future
import com.wix.accord.{validate, Success}
import com.github.slvrthrn.services.{SessionService, UserService}
import com.github.slvrthrn.models.forms.{LoginForm, RegForm}
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class AuthController(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]
  val sessionService = inject[SessionService]

  get("/auth") { request =>
    val authView = new AuthView
    render.html(authView.renderHtml).toFuture
  }

  post("/login") { request =>
    val loginForm = LoginForm(request.getParam("login"), request.getParam("password"))
    validate(loginForm) match {
      case Success =>
        userService.checkLogin(loginForm) flatMap {
          case Some(u: User) =>
            sessionService.createSession(u) flatMap {
              case Some(sid: String) =>
                val cookie = createCookie("sid", sid)
                redirect("/", "Login is OK and session is created").cookie(cookie).toFuture
              case _ => sessionCreationError
            }
          case _ =>
            val errors = Seq(ErrorPayload(
              "Invalid login or password",
              "User service login check failed"))
            renderJsonError(errors, HttpResponseStatus.FORBIDDEN)
        }
      case _ =>
        val errors = Seq(ErrorPayload(
          "Error with form validation: maybe there are empty fields?",
          "LoginForm validation failed"))
        renderJsonError(errors, HttpResponseStatus.BAD_REQUEST)
    }
  }

  post("/reg") { request =>
    val regForm = RegForm(request.getParam("login"), request.getParam("password"))
    validate(regForm) match {
      case Success =>
        userService.createUser(regForm) flatMap {
          case Some(user: User) =>
            sessionService.createSession(user) flatMap {
              case Some(sid: String) =>
                val cookie = createCookie("sid", sid)
                redirect("/", "User creation: success").cookie(cookie).toFuture
              case _ => sessionCreationError
            }
          case _ =>
            val errors = Seq(ErrorPayload(
              "Couldn't create new user, maybe there is registered user with same name?",
              "User service failed to create new user"))
            renderJsonError(errors, HttpResponseStatus.CONFLICT)
        }
      case _ =>
        val errors = Seq(ErrorPayload(
          "Error with form validation: maybe there are empty fields?",
          "RegForm validation failed"))
        renderJsonError(errors, HttpResponseStatus.BAD_REQUEST)
    }
  }

  private def sessionCreationError: Future[ResponseBuilder] = {
    val errors = Seq(ErrorPayload(
      "Couldn't create session",
      "Session service failed to create session"))
    renderJsonError(errors, HttpResponseStatus.INTERNAL_SERVER_ERROR)
  }
}

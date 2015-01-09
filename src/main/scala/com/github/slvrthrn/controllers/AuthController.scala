package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.views.{LoginView, RegView}
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.Future
import com.wix.accord._
import com.github.slvrthrn.services.{SessionService, UserService}
import com.github.slvrthrn.models.forms.{LoginForm, RegForm}
import org.jboss.netty.handler.codec.http.DefaultCookie
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class AuthController(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]
  val sessionService = inject[SessionService]

  get("/login") { request =>
    val loginView = new LoginView
    render.html(loginView.renderHtml).toFuture
  }

  post("/login") { request =>
    val form = parseJsonRequest[LoginForm](request)
    form match {
      case Some(loginForm: LoginForm) =>
        validate(loginForm) match {
          case Success =>
            userService.checkLogin(loginForm) flatMap {
              case Some(u: User) =>
                sessionService.createSession(u) flatMap {
                  case Some(sid: String) =>
                    val cookie = createCookie("sid", sid)
                    renderJsonWithCookie("Login is OK and session is created", cookie)
                  case _ => sessionCreationError
                }
              case _ =>
                val errors = Seq(ErrorPayload(
                  "Invalid login or password",
                  "User service login check failed"))
                renderJsonError(errors, 403)
            }
          case _ =>
            val errors = Seq(ErrorPayload(
              "Error with form validation: maybe there are empty fields?",
              "LoginForm validation failed"))
            renderJsonError(errors, 400)
        }
      case _ =>
        val errors = Seq(ErrorPayload(
          "Malformed login JSON request",
          "Error with parsing LoginForm JSON"))
        renderJsonError(errors, 400)
    }
  }

  get("/reg") { request =>
    val regView = new RegView
    render.html(regView.renderHtml).toFuture
  }

  post("/reg") { request =>
    val form = parseJsonRequest[RegForm](request)
    form match {
      case Some(regForm: RegForm) =>
        validate(regForm) match {
          case Success =>
            userService.createUser(regForm) flatMap {
              case Some(user: User) =>
                sessionService.createSession(user) flatMap {
                  case Some(sid: String) =>
                    val cookie = createCookie("sid", sid)
                    renderJsonWithCookie("User creation: success", cookie)
                  case _ => sessionCreationError
                }
              case _ =>
                val errors = Seq(ErrorPayload(
                  "Couldn't create new user, maybe there is registered user with same name?",
                  "User service failed to create new user"))
                renderJsonError(errors, 409)
            }
          case _ =>
            val errors = Seq(ErrorPayload(
              "Error with form validation: maybe there are empty fields?",
              "RegForm validation failed"))
            renderJsonError(errors, 400)
        }
      case _ =>
        val errors = Seq(ErrorPayload(
          "Malformed registration JSON request",
          "Error with parsing RegForm JSON"))
        renderJsonError(errors, 400)
    }
  }

  private def sessionCreationError: Future[ResponseBuilder] = {
    val errors = Seq(ErrorPayload(
      "Couldn't create session",
      "Session service failed to create session"))
    renderJsonError(errors, 500)
  }
}
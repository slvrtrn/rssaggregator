package com.github.slvrthrn.controllers

import java.util.concurrent.TimeUnit

import com.github.slvrthrn.models.entities.{RssUrl, User}
import com.github.slvrthrn.views.AuthView
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.{Duration, Future}
import com.wix.accord.{validate, Success}
import com.github.slvrthrn.services.{RssService, SessionService, UserService}
import com.github.slvrthrn.models.forms.{LoginForm, RegForm}
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class AuthController(implicit val inj: Injector) extends Controller {

  val userService = inject[UserService]
  val sessionService = inject[SessionService]
  val rssService = inject[RssService]

  get("/api/v1/whoami") { implicit request =>
    withUserContext { user =>
      val urls = rssService.findRssUrlsByUser(user)
      urls flatMap {
        case feed: Seq[RssUrl] => renderJson(WhoAmI(user._id, user.login, feed))
      }
    }
  }

  get("/login") { request =>
    val view = new AuthView
    val html = view.renderHtml(view.LOGIN_TEMPLATE_NAME)
    render.html(html).toFuture
  }

  get("/reg") { request =>
    val view = new AuthView
    val html = view.renderHtml(view.REG_TEMPLATE_NAME)
    render.html(html).toFuture
  }

  get("/logout") { request =>
    val cookie = request.cookies.get("sid")
    cookie match {
      case Some(c: Cookie) =>
        val result = sessionService.destroySession(c.value)
        val expired = new Cookie("sid", "")
        expired.maxAge = Duration(-42, TimeUnit.DAYS)
        result map {
          case true => redirect("/login", "You have successfully logged out").cookie(expired)
          case false => redirect("/login", "Invalid session cookie").cookie(expired)
        }
      case _ => redirect("/login", "You are not logged in").toFuture
    }
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
              case _ =>
                val view = new AuthView
                view.addToModel("error", "Couldn't create session due to internal server error, please try again later")
                val html = view.renderHtml(view.LOGIN_TEMPLATE_NAME)
                render.html(html).toFuture
            }
          case _ =>
            val view = new AuthView
            view.addToModel("error", "Invalid login or password")
            val html = view.renderHtml(view.LOGIN_TEMPLATE_NAME)
            render.html(html).toFuture
        }
      case _ =>
        val view = new AuthView
        view.addToModel("error", "Error with form validation: maybe there are empty fields?")
        val html = view.renderHtml(view.LOGIN_TEMPLATE_NAME)
        render.html(html).toFuture
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
              case _ =>
                val view = new AuthView
                view.addToModel("error", "Couldn't create session due to internal server error, please try again later")
                val html = view.renderHtml(view.REG_TEMPLATE_NAME)
                render.html(html).toFuture
            }
          case _ =>
            val view = new AuthView
            view.addToModel("error", "Couldn't create new user, maybe there is registered user with same name?")
            val html = view.renderHtml(view.REG_TEMPLATE_NAME)
            render.html(html).toFuture
        }
      case _ =>
        val view = new AuthView
        view.addToModel("error", "Error with form validation: maybe there are empty fields?")
        val html = view.renderHtml(view.REG_TEMPLATE_NAME)
        render.html(html).toFuture
    }
  }
}

case class WhoAmI(_id: ObjectId, login: String, feed: Seq[RssUrl])

package com.github.slvrthrn.controllers

import java.nio.charset.Charset

import com.github.slvrthrn.filters.AuthRequest
import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.repositories.UserRepo
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.{Controller => FController, ResponseBuilder, Request => FinatraRequest}
import com.twitter.util.Future
import com.github.slvrthrn.utils.InjectHelper
import org.jboss.netty.handler.codec.http.DefaultCookie
import org.json4s._
import org.json4s.jackson.Serialization.{read, write}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Try, Success}

/**
 * Created by slvr on 12/6/14.
 */
trait Controller extends FController with InjectHelper {

  protected val userRepo: UserRepo = inject[UserRepo]

  protected implicit val executionContext = inject[ExecutionContext]

  protected implicit val formats: Formats = DefaultFormats

  protected def renderJson[T<:AnyRef](data: T)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  protected def renderJsonArray[T<:AnyRef](data: Traversable[T])(implicit m: Manifest[Traversable[T]]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  protected def renderJsonError[T<:AnyRef](errors: Traversable[ErrorPayload], status: Int)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = write(errors)
    render.plain(json).status(status).toFuture
  }

  protected def renderJsonWithCookie[T<:AnyRef](data: T, cookie: Cookie)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).cookie(cookie).toFuture
  }

  protected def parseJsonRequest[T](request: FinatraRequest)(implicit m: Manifest[T]): Option[T] = {
    val jsonString = request.getContent().toString(Charset.forName("UTF-8"))
    val result = Try(read[T](jsonString))
    result match {
      case Success(obj: T) => Some(obj)
      case Failure(e) => None
    }
  }

  protected def renderBadRequest(errors: Seq[ErrorPayload] = Seq(ErrorPayload("Malformed URL", "Invalid ID specified")))
  : Future[ResponseBuilder] = {
    renderJsonError(errors, 400)
  }

  protected def withUserContext(f: User => Future[ResponseBuilder])(implicit request: FinatraRequest): Future[ResponseBuilder] = {
    request.request match {
      case req: AuthRequest =>
        userRepo.findById(req.session.uid) flatMap {
          case Some(u: User) => f(u)
          case _ =>
            val errors = Seq(
              ErrorPayload(
                userMessage = "Invalid session cookie. You will be redirected",
                internalMessage = "Cannot find user with UID stored in session")
            )
            renderJsonError(errors, 403)
        }
      case _ =>
        val errors = Seq(
          ErrorPayload(
            userMessage = "Invalid session cookie. You will be redirected",
            internalMessage = "There is no session in request")
        )
        renderJsonError(errors, 403)
    }
  }

  protected def createCookie(key: String, value: String, path: String = "/", maxAge: Int = 86400) = {
    val dc = new DefaultCookie(key, value)
    dc.setPath(path)
    dc.setMaxAge(maxAge)
    val cookie = new Cookie(dc)
    cookie
  }

}

case class ErrorPayload(userMessage: String, internalMessage: String)
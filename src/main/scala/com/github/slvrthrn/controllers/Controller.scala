package com.github.slvrthrn.controllers

import java.nio.charset.Charset

import com.github.slvrthrn.filters.UserRequest
import com.github.slvrthrn.models.entities.{RssNews, User}
import com.github.slvrthrn.repositories.UserRepo
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.{Controller => FController, ResponseBuilder, Request => FinatraRequest}
import com.twitter.util.Future
import com.github.slvrthrn.utils.InjectHelper
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, DefaultCookie}
import org.json4s.{Formats, DefaultFormats}
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
  protected val defaultCookiePath = "/"
  protected val defaultCookieMaxAge = 86400

  protected def renderJson[T<:AnyRef](data: T)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  protected def renderJsonArray[T<:AnyRef](data: Traversable[T])(implicit m: Manifest[Traversable[T]]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  protected def renderJsonError[T<:AnyRef](errors: Traversable[ErrorPayload], status: HttpResponseStatus)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = write(errors)
    render.plain(json).status(status.getCode).toFuture
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

  protected def withUserContext(f: User => Future[ResponseBuilder])(implicit request: FinatraRequest)
  : Future[ResponseBuilder] = {
    request.request match {
      case req: UserRequest =>
        userRepo.findById(req.session.uid) flatMap {
          case Some(u: User) => f(u)
          case _ =>
            val errors = Seq(
              ErrorPayload(
                "Invalid session cookie. You will be redirected",
                "Cannot find user with UID stored in session")
            )
            renderJsonError(errors, HttpResponseStatus.FORBIDDEN)
        }
      case _ =>
        val errors = Seq(
          ErrorPayload(
            "Invalid session cookie. You will be redirected",
            "There is no session in request")
        )
        renderJsonError(errors, HttpResponseStatus.FORBIDDEN)
    }
  }

  protected def withObjectIdParam(f: ObjectId => Future[ResponseBuilder])(implicit request: FinatraRequest)
  : Future[ResponseBuilder] = {
    val param = request.routeParams.get("id")
    param match {
      case Some(id: String) =>
        val res = Try(new ObjectId(id))
        res match {
          case Success(objectId: ObjectId) => f(objectId)
          case Failure(e) =>
            val errors = Seq(ErrorPayload(
              "Invalid ID specified in request URL",
              "Couldn't parse ObjectId from URL"))
            renderJsonError(errors, HttpResponseStatus.BAD_REQUEST)
        }
      case _ =>
        val errors = Seq(ErrorPayload(
          "ID was not specified in request URL",
          "Couldn't get ID string from URL"))
        renderJsonError(errors, HttpResponseStatus.BAD_REQUEST)
    }
  }

  protected def createCookie(
                              key: String,
                              value: String,
                              path: String = defaultCookiePath,
                              maxAge: Int = defaultCookieMaxAge) = {
    val dc = new DefaultCookie(key, value)
    dc.setPath(path)
    dc.setMaxAge(maxAge)
    val cookie = new Cookie(dc)
    cookie
  }

}

case class ErrorPayload(userMessage: String, internalMessage: String)

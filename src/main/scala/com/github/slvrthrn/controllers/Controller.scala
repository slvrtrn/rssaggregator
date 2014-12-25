package com.github.slvrthrn.controllers

import java.util.concurrent.TimeUnit

import com.github.slvrthrn.filters.AuthRequest
import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.repositories.UserRepo
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.{Controller => FController, ResponseBuilder, Request => FinatraRequest}
import com.twitter.util.{Duration, Future}
import com.github.slvrthrn.utils.InjectHelper
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.Serialization.write

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/6/14.
 */
trait Controller extends FController with InjectHelper {

  protected val userRepo: UserRepo = inject[UserRepo]

  protected implicit val executionContext = inject[ExecutionContext]

  protected implicit val formats: Formats = DefaultFormats

  def renderJson[T<:AnyRef](data: T): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  def renderJsonArray[T<:AnyRef](data: Traversable[T]): Future[ResponseBuilder] = {
    val json = write(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  def renderJsonError[T<:AnyRef](errors: Traversable[ErrorPayload], status: Int): Future[ResponseBuilder] = {
    val json = write(errors)
    render.plain(json).status(status).toFuture
  }

  def withUserContext(f: User => Future[ResponseBuilder])(implicit request: FinatraRequest): Future[ResponseBuilder] = {
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

}

case class ErrorPayload(userMessage: String, internalMessage: String)

package com.github.slvrthrn.controllers

import java.util.concurrent.TimeUnit

import com.github.slvrthrn.filters.AuthRequest
import com.github.slvrthrn.models.entities.User
import com.github.slvrthrn.repositories.UserRepo
import com.novus.salat._
import com.novus.salat.global._
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.{Controller => FController, ResponseBuilder, Request => FinatraRequest}
import com.twitter.util.{Duration, Future}
import com.github.slvrthrn.utils.InjectHelper

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/6/14.
 */
trait Controller extends FController with InjectHelper {

  protected val userRepo: UserRepo = inject[UserRepo]

  protected implicit val executionContext = inject[ExecutionContext]

  def renderJson[T<:AnyRef](data: T)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = grater[T].toCompactJSON(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  def renderJsonArray[T<:AnyRef](data: Traversable[T])(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = grater[T].toCompactJSONArray(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  def withUserContext(f: User => Future[ResponseBuilder])(implicit request: FinatraRequest): Future[ResponseBuilder] = {
    request.request match {
      case req: AuthRequest =>
        userRepo.findById(req.session.uid) flatMap {
          case Some(u: User) => f(u)
          case _ => sessionError("Cannot find user with UID stored in session")
        }
      case _ => sessionError("There is no session in request")
    }
  }

  private def sessionError(s: String): Future[ResponseBuilder] = {
    val expired = new Cookie("sid", "")
    expired.maxAge = Duration(-42, TimeUnit.DAYS)
    render.plain(s).cookie(expired).toFuture
    renderJson(s)
  }

}

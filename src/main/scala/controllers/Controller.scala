package ru.slvr.controllers

import com.novus.salat._
import com.novus.salat.global._
import com.twitter.finatra.{Controller => FController, ResponseBuilder}
import com.twitter.util.Future
import ru.slvr.utils.InjectHelper

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/6/14.
 */
trait Controller extends FController with InjectHelper {
  protected implicit val executionContext = inject[ExecutionContext]

  def renderJson[T<:AnyRef](data: T)(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = grater[T].toCompactJSON(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }

  def renderJsonArray[T<:AnyRef](data: Traversable[T])(implicit m: Manifest[T]): Future[ResponseBuilder] = {
    val json = grater[T].toCompactJSONArray(data)
    render.plain(json).header("Content-Type","application/json").toFuture
  }
}
package com.github.slvrthrn.repositories.cache

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.github.slvrthrn.models.entities.Session
import com.github.slvrthrn.utils.InjectHelper
import com.redis.RedisClient
import scaldi.Injector
import com.novus.salat._
import com.novus.salat.global._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 12/11/14.
 */
class SessionCache(implicit val inj: Injector) extends InjectHelper {

  private val client: RedisClient = inject[RedisClient]

  private implicit val timeout = Timeout(Duration(5, TimeUnit.SECONDS))

  private implicit val executionContext = inject[ExecutionContext]

  def set(session: Session): Future[Boolean] = {
    val json = grater[Session].toCompactJSON(session)
    client.set(cacheKey(session.id), json)
  }

  def get(key: String): Future[Option[Session]] = {
    val json: Future[Option[String]] = client.get(cacheKey(key))
    json map {
      case Some(s: String) => Some(grater[Session].fromJSON(s))
      case _ => None
    }
  }

  private def cacheKey(key: String) = "sessions_id." + key

}

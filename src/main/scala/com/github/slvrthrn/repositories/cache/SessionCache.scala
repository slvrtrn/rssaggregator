package com.github.slvrthrn.repositories.cache

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.github.slvrthrn.models.Session
import com.github.slvrthrn.utils.InjectHelper
import com.redis.RedisClient
import scaldi.Injector
import com.novus.salat._
import com.novus.salat.global._
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 12/11/14.
 */
class SessionCache(implicit val inj: Injector) extends InjectHelper {

  private val client: RedisClient = inject[RedisClient]

  private implicit val timeout = Timeout(Duration(5, TimeUnit.SECONDS))

  def put(session: Session): Future[Boolean] = {
    val json = grater[Session].toCompactJSON(session)
    client.set(session.id, json)
  }

  def get(key: String): Future[Option[String]] = {
    client.get(key)
  }

}

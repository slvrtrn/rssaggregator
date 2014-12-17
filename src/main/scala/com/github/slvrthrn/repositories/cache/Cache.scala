package com.github.slvrthrn.repositories.cache

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.github.slvrthrn.utils.InjectHelper
import com.redis.RedisClient
import com.twitter.util.Future

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import com.novus.salat._
import com.novus.salat.global._
import com.github.slvrthrn.utils.Twitter._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Success

/**
 * Created by slvr on 12/16/14.
 */
trait Cache { self: InjectHelper =>
  def cacheFor[A <: AnyRef](namespace: String)(implicit ec: ExecutionContext, m: Manifest[A]) = new CacheBuilder[A](namespace, inject[RedisClient])
}

case class CacheBuilder[A <: AnyRef](namespace: String, redisClient: RedisClient)(implicit ec: ExecutionContext, m: Manifest[A]) {

  private val client = redisClient

  private implicit val timeout = Timeout(Duration(5, TimeUnit.SECONDS))

  def getOrElseUpdate(key: Any)(orElse: => Future[A]): Future[A] = {
    val cachedF = client.get[String](cacheKey(key)).asTwitter
    cachedF flatMap {
      case Some(str: String) => Future value grater[A].fromJSON(str)
      case None => orElse onSuccess { inst: A => save(key, inst) }
    }
  }

  def save(key: Any, inst: A): Future[A] = {
    val str = grater[A].toCompactJSON(inst)
    Future {
      client.set(cacheKey(key), str)
      inst
    }
  }

  private def cacheKey(key: Any) = namespace + "." + key.toString

}
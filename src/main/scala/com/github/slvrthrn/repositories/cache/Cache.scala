package com.github.slvrthrn.repositories.cache

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.github.slvrthrn.utils.InjectHelper
import com.redis.RedisClient
import com.twitter.util.Future
import org.json4s._
import org.json4s.mongo.ObjectIdSerializer
import org.json4s.native.Serialization.{read, write}

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import com.github.slvrthrn.utils.Twitter._

/**
 * Created by slvr on 12/16/14.
 */
trait Cache { self: InjectHelper =>
  def cacheFor[A <: AnyRef](namespace: String)(implicit ec: ExecutionContext, m: Manifest[A])
  = new CacheBuilder[A](namespace, inject[RedisClient])
}

case class CacheBuilder[A <: AnyRef](namespace: String, redisClient: RedisClient)
                                    (implicit ec: ExecutionContext, m: Manifest[A]) {

  protected val client = redisClient

  protected implicit val timeout = Timeout(Duration(5, TimeUnit.SECONDS))

  protected implicit val formats: Formats = DefaultFormats + new ObjectIdSerializer

  def getOrElseUpdate(key: Any)(orElse: => Future[A]): Future[A] = {
    val cachedF = client.get[String](cacheKey(key)).asTwitter
    cachedF flatMap {
      case Some(str: String) => Future value read[A](str)
      case _ => orElse onSuccess {
        case inst: A => save(key, inst)
        case _ => Future value None
      }
    }
  }

  def save(key: Any, inst: A): Future[A] = {
    val str = write[A](inst)
    Future {
      client.set(cacheKey(key), str)
      inst
    }
  }

  private def cacheKey(key: Any) = namespace + "." + key.toString

}
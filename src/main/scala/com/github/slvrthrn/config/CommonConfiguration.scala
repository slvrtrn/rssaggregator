package com.github.slvrthrn.config

import akka.actor.ActorSystem
import com.mongodb.casbah._
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.redis.RedisClient
import com.typesafe.config.{ConfigFactory, Config}
import scaldi.Module

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/6/14.
 */
class CommonConfiguration extends Configuration {

  bind[MongoClient] to mongoClient("mongodb.default.uri")
  bind[MongoDB] to mongoDb("mongodb.default.db")
  bind[Config] to ConfigFactory.load()
  bind[ExecutionContext] to scala.concurrent.ExecutionContext.Implicits.global
  bind[RedisClient] to redisClient("redis.default.url", "redis.default.port")

}

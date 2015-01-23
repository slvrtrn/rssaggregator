package com.github.slvrthrn.config

import com.mongodb.casbah.{MongoDB, MongoClient}
import com.redis.RedisClient
import com.typesafe.config.{ConfigFactory, Config}

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 23.01.15.
 */
class TestConfiguration extends Configuration {

  bind[MongoClient] to mongoClient("mongodb.test.uri")
  bind[MongoDB] to mongoDb("mongodb.test.db")
  bind[Config] to ConfigFactory.load()
  bind[ExecutionContext] to scala.concurrent.ExecutionContext.Implicits.global
  bind[RedisClient] to redisClient("redis.test.url", "redis.test.port")

}

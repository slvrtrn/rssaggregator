package com.github.slvrthrn.config

import akka.actor.ActorSystem
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.mongodb.casbah.{MongoDB, MongoClientURI, MongoClient}
import com.redis.RedisClient
import com.typesafe.config.Config
import scaldi.Module

/**
 * Created by slvr on 23.01.15.
 */
class Configuration extends Module {

  def mongoClient(configPath: String): MongoClient = {
    val config = inject[Config]
    val mongoUriString = config.getString(configPath)
    val uri = MongoClientURI(mongoUriString)
    RegisterJodaTimeConversionHelpers()
    MongoClient(uri)
  }

  def mongoDb(configPath: String): MongoDB = {
    val config = inject[Config]
    val mongoClient = inject[MongoClient]
    val dbName = config.getString(configPath)
    mongoClient(dbName)
  }

  def redisClient(urlConfigPath: String, portConfigPath: String): RedisClient = {
    implicit val system = ActorSystem("redis-client")
    val config = inject[Config]
    val url = config.getString(urlConfigPath)
    val port = config.getInt(portConfigPath)
    RedisClient(url, port)
  }

}

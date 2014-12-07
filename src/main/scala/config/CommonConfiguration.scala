package ru.slvr.config

import com.mongodb.casbah._
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.typesafe.config.{ConfigFactory, Config}
import scaldi.Module

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/6/14.
 */
class CommonConfiguration extends Module {
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

  bind [MongoClient] to mongoClient("mongodb.default.uri")
  bind [MongoDB] to mongoDb("mongodb.default.db")
  bind [Config] to ConfigFactory.load()
  bind [ExecutionContext] to scala.concurrent.ExecutionContext.Implicits.global
}

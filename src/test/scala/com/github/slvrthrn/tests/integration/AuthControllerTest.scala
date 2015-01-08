package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.AuthController
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.{SpecHelper, FlatSpecHelper}
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.json4s._
import org.json4s.mongo.ObjectIdSerializer
import org.json4s.native.Serialization.{read, write}

/**
* Created by slvr on 1/8/15.
*/
class AuthControllerTest extends FlatSpecHelper with Matchers with InjectHelper {

  implicit val inj = BindingsProvider.getBindings
  override def server: FinatraServer = new TestServer
  server.register(new AuthController)
  implicit val formats = DefaultFormats

  def parseJson[T](jsonString: String)(implicit m: Manifest[T]): T = {
    try {
      read[T](jsonString)
    } catch {
      case e: Exception => throw new Exception("can`t parse string [" + jsonString + "]", e)
    }
  }

  def postJson(path: String, json: String = "", headers: Map[String, String] = Map()) = {
    val headersWithContentType = headers ++ Map("Content-Type" -> "application/json")
    post(
      path = path,
      body = json,
      headers = headersWithContentType
    )
  }

}

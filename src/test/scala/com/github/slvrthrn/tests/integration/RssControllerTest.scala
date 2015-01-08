package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.RssController
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.FlatSpecHelper
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._
import org.scalatest.{Ignore, Matchers, BeforeAndAfterAll}
import scaldi.Injector

/**
 * Created by slvr on 1/9/15.
 */
@Ignore
class RssControllerTest extends FlatSpecHelper with BeforeAndAfterAll with Matchers {

  implicit val formats = DefaultFormats
  implicit val inj: Injector = BindingsProvider.getBindings

  override def server: FinatraServer = new TestServer {
    register(new RssController)
  }

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

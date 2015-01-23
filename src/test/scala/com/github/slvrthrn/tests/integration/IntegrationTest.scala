package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.twitter.finatra.test.FlatSpecHelper
import org.json4s.DefaultFormats
import org.json4s.ext.DateTimeSerializer
import org.json4s.jackson.Serialization._
import org.scalatest.{Matchers, BeforeAndAfterAll}
import scaldi.Injector

/**
 * Created by slvr on 11.01.15.
 */
trait IntegrationTest extends FlatSpecHelper with BeforeAndAfterAll with Matchers {

  implicit val formats = DefaultFormats + DateTimeSerializer
  implicit val inj: Injector = BindingsProvider.getTestBindings

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

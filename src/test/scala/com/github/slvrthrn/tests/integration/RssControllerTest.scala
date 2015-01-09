package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.RssController
import com.github.slvrthrn.filters.IndexFilter
import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.entities.{RssUrl, User}
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.FlatSpecHelper
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import org.scalatest.{Ignore, Matchers, BeforeAndAfterAll}
import scaldi.Injector

/**
 * Created by slvr on 1/9/15.
 */
//@Ignore
class RssControllerTest extends FlatSpecHelper with BeforeAndAfterAll with Matchers {

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomRegPwd = helper.randomRegPwd
    user = helper.registerUser(randomRegLogin, randomRegPwd)
    rss = helper.getRssUrlStrFromConfig()
    sid = helper.getSessionId(user)
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.deleteRssUrl(rss)
    helper.clearCache
  }

  implicit val inj: Injector = BindingsProvider.getBindings
  implicit val formats = DefaultFormats
  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomRegPwd: String = _
  var user: User = _
  var rss: String = _
  var sid: String = _

  override def server = new TestServer {
    withUserContext(user)
    //addFilter(new IndexFilter)
    register(new RssController)
  }

  it should "add new RSS in user's feed" in {
    val json = write[String](rss)
    postJson("/api/v1/urls", json)
    response.status should equal (HttpResponseStatus.OK)
  }

  it should "show user's RSS subscriptions list" in {
    get("/api/v1/urls")
    val rssUrl = helper.getRssUrl(rss)
    val result = parseJson[Seq[RssUrl]](response.body)
    response.status should equal (HttpResponseStatus.OK)
    result should have size 1
    result.head.url should equal (rssUrl.url)
  }

  it should "delete RSS from user's subscriptions list" in {
    val updatedUser = helper.getUser(randomRegLogin, randomRegPwd)
    delete(s"/api/v1/urls/${updatedUser.feed.head.toString}")
    response.status should equal (HttpResponseStatus.OK)
  }

  it should "response with 404 not found" in {
    val randomObjectId = new ObjectId().toString
    delete(s"/api/v1/urls/$randomObjectId")
    response.status should equal (HttpResponseStatus.NOT_FOUND)
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

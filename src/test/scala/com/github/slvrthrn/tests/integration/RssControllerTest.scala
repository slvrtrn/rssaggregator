package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.{ErrorPayload, RssController}
import com.github.slvrthrn.filters.IndexFilter
import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.dto.RssUrlDto
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
class RssControllerTest extends IntegrationTest {

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
    val json = write(RssUrlDto(rss))
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

  it should "response with 400 bad request because URL is malformed" in {
    val json = write(RssUrlDto("randomsitename.ru/rss"))
    postJson("/api/v1/urls", json)
    val result = parseJson[Seq[ErrorPayload]](response.body)
    response.status should equal (HttpResponseStatus.BAD_REQUEST)
    result.head.userMessage should equal ("Invalid URL format")
  }

  it should "response with 400 bad request because URL is not valid RSS source" in {
    val json = write(RssUrlDto("http://google.ru/"))
    postJson("/api/v1/urls", json)
    val result = parseJson[Seq[ErrorPayload]](response.body)
    response.status should equal (HttpResponseStatus.BAD_REQUEST)
    result.head.userMessage should equal ("Submitted URL doesn't seem to be valid RSS source")
  }

  it should "response with 404 not found" in {
    val randomObjectId = new ObjectId().toString
    delete(s"/api/v1/urls/$randomObjectId")
    response.status should equal (HttpResponseStatus.NOT_FOUND)
  }

}

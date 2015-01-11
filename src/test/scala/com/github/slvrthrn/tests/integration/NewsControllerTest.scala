package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.NewsController
import com.github.slvrthrn.filters.IndexFilter
import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.entities.{RssNews, User}
import com.twitter.finatra.test.FlatSpecHelper
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import org.scalatest.{Ignore, Matchers, BeforeAndAfterAll}
import scaldi.Injector

/**
 * Created by slvr on 09.01.15.
 */
//@Ignore
class NewsControllerTest extends IntegrationTest {

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomRegPwd = helper.randomRegPwd
    rss = helper.getRssUrlStrFromConfig()
    user = helper.insertRssUrl(rss, helper.registerUser(randomRegLogin, randomRegPwd))
    sid = helper.getSessionId(user)
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.deleteRssUrl(rss)
    helper.clearNewsCollection
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
    register(new NewsController)
  }

  it should "render all news from user's subscription" in {
    get("/api/v1/news")
    val result = parseJson[Seq[RssNews]](response.body)
    result.size should be > 0
  }

  it should "render specific news item" in {
    val item = helper.getNews(user).head
    get(s"/api/v1/news/${item._id.toString}")
    val result = parseJson[RssNews](response.body)
    result.title should equal (item.title)
    result.link should equal (item.link)
    result.description should equal (item.description)
  }

  it should "use range-based pagination correctly" in {
    val news = helper.getNews(user)
    val objectId = news(4)._id
    get(s"/api/v1/news/start/${objectId.toString}")
    val result = parseJson[Seq[RssNews]](response.body)
    result.size should be > 0
  }

  it should "response with 404 not found for specific news item" in {
    val randomObjectId = new ObjectId().toString
    get(s"/api/v1/news/$randomObjectId")
    response.status should equal (HttpResponseStatus.NOT_FOUND)
  }

  it should "response with 400 bad request for specific news item" in {
    get("/api/v1/news/randomtext")
    response.status should equal (HttpResponseStatus.BAD_REQUEST)
  }

  it should "render all news with specific rss url" in {
    val objectId = user.feed.head
    get(s"/api/v1/news/url/$objectId")
    val result = parseJson[Seq[RssNews]](response.body)
    result.size should be > 0
  }

  it should "response with 400 bad request for news list with specific url" in {
    get("/api/v1/news/url/randomtext")
    response.status should equal (HttpResponseStatus.BAD_REQUEST)
  }

}

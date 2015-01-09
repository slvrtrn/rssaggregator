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
class NewsControllerTest extends FlatSpecHelper with BeforeAndAfterAll with Matchers {

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

  implicit val inj: Injector = BindingsProvider.getBindings
  implicit val formats = DefaultFormats
  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomRegPwd: String = _
  var user: User = _
  var rss: String = _
  var sid: String = _

  override def server = new TestServer {
    //withUserContext(user)
    addFilter(new IndexFilter)
    register(new NewsController)
  }

  it should "render all news from user's subscription" in {
    get("/api/v1/news", headers = Map("Cookie" -> s"sid=$sid"))
    val result = parseJson[Seq[RssNews]](response.body)
    result.size should be > 0
  }

  it should "render specific news item" in {
    val item = helper.getNews(user).head
    get(s"/api/v1/news/${item._id.toString}", headers = Map("Cookie" -> s"sid=$sid"))
    val result = parseJson[RssNews](response.body)
    result.title should equal (item.title)
    result.link should equal (item.link)
    result.description should equal (item.description)
  }

  it should "response with 404 not found" in {
    val randomObjectId = new ObjectId().toString
    get(s"/api/v1/news/$randomObjectId", headers = Map("Cookie" -> s"sid=$sid"))
    response.status should equal (HttpResponseStatus.NOT_FOUND)
  }

  it should "response with 400 bad request" in {
    get("/api/v1/news/randomtext", headers = Map("Cookie" -> s"sid=$sid"))
    response.status should equal (HttpResponseStatus.BAD_REQUEST)
  }

  def parseJson[T](jsonString: String)(implicit m: Manifest[T]): T = {
    try {
      read[T](jsonString)
    } catch {
      case e: Exception => throw new Exception("can`t parse string [" + jsonString + "]", e)
    }
  }

}

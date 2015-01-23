package com.github.slvrthrn.helpers

import java.net.URL
import java.util.UUID
import java.util.concurrent.TimeUnit
import akka.util.Timeout
import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.models.entities.{RssNews, RssUrl, User}
import com.github.slvrthrn.models.forms.{RegForm, LoginForm}
import com.github.slvrthrn.repositories.{RssNewsRepo, RssUrlRepo, UserRepo}
import com.github.slvrthrn.services._
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.Imports._
import com.redis.RedisClient
import com.redis.protocol.KeyCommands
import com.twitter.finagle.http.Cookie
import com.twitter.finatra.test.MockResponse
import com.twitter.util.Future
import com.typesafe.config.Config
import com.github.slvrthrn.utils.Twitter._
import org.jboss.netty.handler.codec.http.CookieDecoder
import scala.concurrent.{Future => ScalaFuture}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.xml.XML

/**
 * Created by slvr on 1/8/15.
 */
class TestHelper extends InjectHelper {

  implicit val inj = BindingsProvider.getTestBindings
  implicit val redisTimeout = Timeout(Duration(5, TimeUnit.SECONDS))
  val awaitTimeout = Duration.Inf
  val rssService = inject[RssService]
  val userService = inject[UserService]
  val sessionService = inject[SessionService]
  val userRepo = inject[UserRepo]
  val urlRepo = inject[RssUrlRepo]
  val newsRepo = inject[RssNewsRepo]
  val redisClient = inject[RedisClient]
  val config = inject[Config]

  def registerUser(login: String = randomRegLogin, password: String = randomRegPwd): User = {
    val regForm = RegForm(login, password)
    val futureReg = userService.createUser(regForm).asScala
    Await.result(futureReg, awaitTimeout).get
  }

  def getUser(login: String, password: String): User = {
    val futureUser = userService.checkLogin(LoginForm(login, password)).asScala
    Await.result(futureUser, awaitTimeout).get
  }

  def getSessionId(u: User): String = {
    val futureSession = sessionService.createSession(u).asScala
    Await.result(futureSession, awaitTimeout).get
  }

  def insertRssUrl(rssUrl: String, user: User): User = {
    val url = new URL(rssUrl: String)
    val xml = XML.load(url)
    val futureAdd = rssService.addRssUrl(url, user, xml).asScala
    Await.result(futureAdd, awaitTimeout)
  }

  def deleteRssUrlFromUser(url: RssUrl, user: User): Boolean = {
    val futureRemove = rssService.removeRssUrl(url._id, user).asScala
    Await.result(futureRemove, awaitTimeout)
  }

  def deleteRssUrl(url: String): Future[Boolean] = {
    urlRepo.removeBy("url" $eq url)
  }

  def getNews(user: User): Seq[RssNews] = {
    val limit = config.getInt("app.default.news.onPageLimit")
    val futureNews = rssService.getNews(user, limit).asScala
    Await.result(futureNews, awaitTimeout)
  }

  def clearCache: ScalaFuture[KeyCommands.FlushDB.Ret] = redisClient.flushdb

  def deleteUser(login: String): Future[Boolean] = userRepo.removeBy("login" $eq login)

  def clearNewsCollection: Future[WriteResult] = newsRepo.clearCollection

  def randomRegLogin: String = UUID.randomUUID.toString

  def randomRegPwd: String = UUID.randomUUID.toString

  def getRssUrlStrFromConfig(i: Int = 1): String = config.getString(s"test.default.rssUrl$i")

  def getRssUrl(urlStr: String): RssUrl = {
    val futureRssUrl = urlRepo.findByUrl(urlStr).asScala
    Await.result(futureRssUrl, awaitTimeout).get
  }

  def cookie(name: String, response: MockResponse): Option[Cookie] = {
    import scala.collection.JavaConverters._
    val cookies = response.getHeaders.getAll("Set-Cookie").flatMap { encoded =>
      new CookieDecoder().decode(encoded).asScala
    }
    cookies.find(_.getName == name).map(new Cookie(_))
  }

  def cookieValue(name: String, response: MockResponse): Option[String] = {
    cookie(name, response).map(_.value)
  }

}

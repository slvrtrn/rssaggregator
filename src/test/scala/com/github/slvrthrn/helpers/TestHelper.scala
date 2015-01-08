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
import com.twitter.util.Future
import com.typesafe.config.Config
import com.github.slvrthrn.utils.Twitter._
import org.json4s.native.Serialization._
import scala.concurrent.{Future => ScalaFuture}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 1/8/15.
 */
class TestHelper extends InjectHelper {

  implicit val inj = BindingsProvider.getBindings
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

  def insertRssUrl(rssUrl: String, user: User): RssUrl = {
    val url = new URL(rssUrl: String)
    val futureAdd = rssService.addRssUrl(url, user).asScala
    Await.result(futureAdd, awaitTimeout).get
  }

  def deleteRssUrlFromUser(url: RssUrl, user: User): Boolean = {
    val futureRemove = rssService.removeRssUrl(url._id.toString, user).asScala
    Await.result(futureRemove, awaitTimeout)
  }

  def deleteRssUrl(url: String): Future[Boolean] = {
    urlRepo.removeBy("url" $eq url)
  }

  def loadNews(user: User): Seq[RssNews] = {
    val futureNews = rssService.getNews(user).asScala
    Await.result(futureNews, awaitTimeout)
  }

  def clearCache: ScalaFuture[KeyCommands.FlushDB.Ret] = redisClient.flushdb

  def deleteUser(login: String): Future[Boolean] = userRepo.removeBy("login" $eq login)

  def clearNewsCollection: Future[WriteResult] = newsRepo.clearCollection

  def randomRegLogin: String = UUID.randomUUID.toString

  def randomRegPwd: String = UUID.randomUUID.toString

  def getRssUrl: String = config.getString("test.default.rssUrl")

}
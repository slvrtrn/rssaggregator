package com.github.slvrthrn

import java.net.URL
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.models.entities.{RssUrl, Session, User}
import com.github.slvrthrn.models.forms.{RegForm, LoginForm}
import com.github.slvrthrn.repositories.{RssNewsRepo, RssUrlRepo, UserRepo}
import com.github.slvrthrn.services._
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.Imports._
import com.redis.RedisClient
import com.typesafe.config.Config
import org.bson.types.ObjectId
import com.github.slvrthrn.utils.Twitter._
import org.scalatest._
import org.json4s._
import org.json4s.mongo.ObjectIdSerializer
import org.json4s.native.Serialization.{read, write}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 12/17/14.
 */

class Test extends FlatSpec with Matchers with InjectHelper with BeforeAndAfterAll {

  implicit val inj = BindingsProvider.getBindings
  implicit val formats: Formats = DefaultFormats + new ObjectIdSerializer
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

  val randomRegLogin = UUID.randomUUID.toString
  val randomPwd = UUID.randomUUID.toString
  val rssUrl = config.getString("test.default.rssUrl")

  it should "register user successfully" in {
    val u = registerUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal(randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Set[ObjectId]] should be (true)
    u.feed should be ('empty)
  }
  
  it should "process user login successfully" in {
    val u = getUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal (randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Set[ObjectId]] should be (true)
  }

  it should "create session successfully" in {
    val user = getUser(randomRegLogin, randomPwd)
    val sid = getSessionId(user)
    sid should have length 36
  }

  it should "insert new RSS URL successfully and then delete it with ObjectId" in {
    val user = getUser(randomRegLogin, randomPwd)
    val url = insertRssUrl(rssUrl, user)
    url.isInstanceOf[RssUrl] should be (true)
    val updatedUser = getUser(randomRegLogin, randomPwd)
    val futureRemove = rssService.removeRssUrl(url._id.toString, updatedUser).asScala
    val result = Await.result(futureRemove, awaitTimeout)
    result should be (true)
  }

  it should "insert new RSS URL successfully again" in {
    val user = getUser(randomRegLogin, randomPwd)
    val result = insertRssUrl(rssUrl, user)
    result.isInstanceOf[RssUrl] should be (true)
  }

  it should "download and parse news into the sequence" in {
    val user = getUser(randomRegLogin, randomPwd)
    val futureNews = rssService.loadNews(user).asScala
    val result = Await.result(futureNews, awaitTimeout)
    result.size should be > 0
  }

//  it should "serialize and deserialize Option[User] and Seq[User] via json4s" in {
//    val optionUser = Some(getUser(randomRegLogin, randomPwd))
//    val seqOfUsers = Seq(optionUser, optionUser)
//    val optionSer = write(optionUser)
//    val seqSer = write(seqOfUsers)
//    val user = read[Option[User]](optionSer).get
//    user.isInstanceOf[User] should be (true)
//    user.login should equal(randomRegLogin)
//    user.password should have length 60
//    val seq = read[Seq[User]](seqSer)
//    seq.isInstanceOf[Seq[User]] should be (true)
//    seq should have length 2
//  }

  override def afterAll() = {
    userRepo.removeBy("login" $eq randomRegLogin)
    urlRepo.removeBy("url" $eq rssUrl)
    newsRepo.clearCollection
    redisClient.flushdb
  }

  def registerUser(login: String, password: String): User = {
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

}
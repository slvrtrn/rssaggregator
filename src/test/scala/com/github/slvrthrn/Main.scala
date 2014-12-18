package com.github.slvrthrn

import java.net.URL
import java.util.UUID

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.models.entities.{Session, User}
import com.github.slvrthrn.models.forms.{RegForm, LoginForm}
import com.github.slvrthrn.repositories.{RssUrlRepo, UserRepo}
import com.github.slvrthrn.services._
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import com.typesafe.config.Config
import org.bson.types.ObjectId
import com.github.slvrthrn.utils.Twitter._
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 12/17/14.
 */

class Test extends FlatSpec with Matchers with InjectHelper with BeforeAndAfterAll {

  implicit val inj = BindingsProvider.getBindings

  val timeout = Duration.Inf
  val rssService = inject[RssService]
  val userService = inject[UserService]
  val sessionService = inject[SessionService]
  val userRepo = inject[UserRepo]
  val urlRepo = inject[RssUrlRepo]
  val config = inject[Config]

  val randomRegLogin = UUID.randomUUID.toString
  val randomPwd = UUID.randomUUID.toString
  val rssUrl = config.getString("test.default.rssUrl")

  it should "register user successfully" in {
    val u = registerUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal(randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Map[String, String]] should be (true)
    u.feed should be ('empty)
  }
  
  it should "process user login successfully" in {
    val u = getUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal (randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Map[String, String]] should be (true)
  }

  it should "create session successfully" in {
    val user = getUser(randomRegLogin, randomPwd)
    val sid = getSessionId(user)
    sid should have length 36
  }

  it should "insert new RSS URL successfully" in {
    val url = new URL(rssUrl)
    val user = getUser(randomRegLogin, randomPwd)
    val futureAdd = rssService.addRssUrl(url, user).asScala
    val result = Await.result(futureAdd, timeout)
    result should be (true)
  }

  it should "download and parse news into the list" in {
    val user = getUser(randomRegLogin, randomPwd)
    val futureNews = rssService.loadNews(user).asScala
    val result = Await.result(futureNews, timeout)
    result.size should be > 0
  }

  override def afterAll() = {
    userRepo.removeBy(MongoDBObject("login" -> randomRegLogin))
    urlRepo.removeBy(MongoDBObject("url" -> rssUrl))
  }

  def registerUser(login: String, password: String): User = {
    val regForm = RegForm(login, password)
    val futureReg = userService.createUser(regForm).asScala
    Await.result(futureReg, timeout).get
  }

  def getUser(login: String, password: String): User = {
    val futureUser = userService.checkLogin(LoginForm(login, password)).asScala
    Await.result(futureUser, timeout).get
  }

  def getSessionId(u: User): String = {
    val futureSession = sessionService.createSession(u).asScala
    Await.result(futureSession, timeout).get
  }

}
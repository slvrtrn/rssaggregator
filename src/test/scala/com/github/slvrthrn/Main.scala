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
import org.bson.types.ObjectId
import com.github.slvrthrn.utils.Twitter._
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by slvr on 12/17/14.
 */

class Test extends FlatSpec with Matchers with InjectHelper with BeforeAndAfter {

  implicit val inj = BindingsProvider.getBindings

  val randomRegLogin = UUID.randomUUID.toString
  val rssUrl = "http://www.overclockers.ru/rss/lab.rss"
  val login = "test"
  val pwd = "test"
  
  val timeout = Duration.Inf
  val rssService = inject[RssService]
  val userService = inject[UserService]
  val sessionService = inject[SessionService]
  val userRepo = inject[UserRepo]
  val urlRepo = inject[RssUrlRepo]

  it should "process user login successfully" in {
    val u = getUser(login, pwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal (login)
    u.password should have length 60
    u.feed.isInstanceOf[Set[String]] should be (true)
  }

  it should "create session successfully" in {
    val user = getUser(login, pwd)
    val sid = getSessionId(user)
    sid should have length 36
  }

  it should "register user successfully" in {
    val u = registerUser
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal(randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Set[String]] should be (true)
    u.feed should be ('empty)
  }

  it should "insert new RSS URL successfully" in {
    val url = new URL("http://www.overclockers.ru/rss/lab.rss")
    val user = getUser(login, pwd)
    val futureAdd = rssService.addRssUrl(url, user).asScala
    val result = Await.result(futureAdd, timeout)
    result.isInstanceOf[WriteResult] should be (true)
  }

  after {
    userRepo.removeBy(MongoDBObject("login" -> randomRegLogin))
    urlRepo.removeBy(MongoDBObject("url" -> rssUrl))
    userRepo.pull(
      MongoDBObject("login" -> login),
      Map[String, String]("feed" -> rssUrl)
    )
  }

  def getUser(login: String, password: String): User = {
    val futureUser = userService.checkLogin(LoginForm(login, password)).asScala
    Await.result(futureUser, timeout).get
  }

  def getSessionId(u: User): String = {
    val futureSession = sessionService.createSession(u).asScala
    Await.result(futureSession, timeout).get
  }

  def registerUser: User = {
    val regForm = RegForm(randomRegLogin, "password")
    val futureReg = userService.createUser(regForm).asScala
    Await.result(futureReg, timeout).get
  }

}
package com.github.slvrthrn

import java.util.UUID

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.models.entities.{Session, User}
import com.github.slvrthrn.models.forms.{RegForm, LoginForm}
import com.github.slvrthrn.repositories.UserRepo
import com.github.slvrthrn.services._
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._
import scaldi.Injector
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
  val timeout = Duration.Inf
  val rssService = inject[RssService]
  val userService = inject[UserService]
  val sessionService = inject[SessionService]
  val userRepo = inject[UserRepo]

  it should "process user login successfully" in {
    getUser("test", "test").login should equal ("test")
  }

  it should "create session successfully" in {
    val user = getUser("test", "test")
    val sid = getSessionId(user)
    sid should have length 36
  }

  it should "register user successfully" in {
    val newUser = registerUser
    newUser.login should equal(randomRegLogin)
    newUser.password should have length 60
  }

  after {
    userRepo.removeBy(MongoDBObject("login" -> randomRegLogin))
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
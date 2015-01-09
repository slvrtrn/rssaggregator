package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.AuthController
import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.forms.{LoginForm, RegForm}
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.FlatSpecHelper
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import org.scalatest.{Ignore, BeforeAndAfterAll, Matchers}
import scaldi.Injector

/**
* Created by slvr on 1/8/15.
*/
//@Ignore
class AuthControllerTest extends FlatSpecHelper with BeforeAndAfterAll with Matchers {

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomRegPwd = helper.randomRegPwd
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.clearCache
  }

  implicit val formats = DefaultFormats
  implicit val inj: Injector = BindingsProvider.getBindings
  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomRegPwd: String = _

  override def server: FinatraServer = new TestServer {
    register(new AuthController)
  }

  it should "show login page" in {
    get("/login")
    response.body should include ("Login")
    response.body should include ("<input name=\"login\" type=\"text\">")
    response.body should include ("<input name=\"password\" type=\"password\">")
  }

  it should "show registration page" in {
    get("/reg")
    response.body should include ("Registration")
    response.body should include ("<input name=\"login\" type=\"text\">")
    response.body should include ("<input name=\"password\" type=\"password\">")
  }

  it should "register new user successfully via POST JSON request and create new session" in {
    val regForm = RegForm(login = randomRegLogin, password = randomRegPwd)
    val json = write(regForm)
    postJson("/reg", json)
    val result = parseJson[String](response.body)
    response.status should equal (HttpResponseStatus.OK)
    result should equal ("User creation: success")
    val sid = helper.cookieValue("sid", response).get
    sid should have length 36
  }

  it should "login newly registered user via POST JSON request and create new session" in {
    val loginForm = LoginForm(login = randomRegLogin, password = randomRegPwd)
    val json = write(loginForm)
    postJson("/login", json)
    val result = parseJson[String](response.body)
    response.status should equal (HttpResponseStatus.OK)
    result should equal ("Login is OK and session is created")
    val sid = helper.cookieValue("sid", response).get
    sid should have length 36
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

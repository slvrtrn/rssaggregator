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
class AuthControllerTest extends IntegrationTest {

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomRegPwd = helper.randomRegPwd
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.clearCache
  }

  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomRegPwd: String = _

  override def server: FinatraServer = new TestServer {
    register(new AuthController)
  }

  it should "show login and registration page" in {
    get("/auth")
    response.body should include ("<input name=\"login\" type=\"text\">")
    response.body should include ("<input name=\"password\" type=\"password\">")
  }

  it should "register new user successfully via POST JSON request and redirect to index" in {
    post("/reg", Map("login" -> randomRegLogin, "password" -> randomRegPwd))
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should equal ("User creation: success")
    val sid = helper.cookieValue("sid", response).get
    sid should have length 36
  }

  it should "login newly registered user via POST JSON request and redirect to index" in {
    post("/login", Map("login" -> randomRegLogin, "password" -> randomRegPwd))
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should equal ("Login is OK and session is created")
    val sid = helper.cookieValue("sid", response).get
    sid should have length 36
  }

}

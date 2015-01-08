package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.AuthController
import com.github.slvrthrn.helpers.TestHelper
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.FlatSpecHelper
import org.jboss.netty.handler.codec.http.HttpResponseStatus
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

  it should "register new user successfully via POST" in {
    post("/reg", Map("login" -> randomRegLogin, "password" -> randomRegPwd))
    response.status should equal (HttpResponseStatus.OK)
    response.body should include ("User creation: success")
  }

  it should "login newly registered user via POST and create session" in {
    post("/login", Map("login" -> randomRegLogin, "password" -> randomRegPwd))
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should include ("Login is OK and session is created")
    val sid = helper.cookieValue("sid", response).get
    sid should have length 36
  }

}

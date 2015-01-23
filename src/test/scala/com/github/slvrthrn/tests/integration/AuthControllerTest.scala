package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.{WhoAmI, AuthController}
import com.github.slvrthrn.filters.IndexFilter
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
    addFilter(new IndexFilter)
    register(new AuthController)
  }

  it should "show login page" in {
    get("/login")
    response.body should include("Login")
    response.body should include ("<input class=\"form-control\" placeholder=\"Username\" name=\"login\" type=\"text\">")
    response.body should include ("<input class=\"form-control\" placeholder=\"Password\" name=\"password\" type=\"password\">")
  }

  it should "show reg page" in {
    get("/reg")
    response.body should include("Registration")
    response.body should include ("<input class=\"form-control\" placeholder=\"Preferred username\" name=\"login\" type=\"text\">")
    response.body should include ("<input class=\"form-control\" placeholder=\"Password\" name=\"password\" type=\"password\">")
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

  it should "perform logout" in {
    val user = helper.getUser(randomRegLogin, randomRegPwd)
    val sid = helper.getSessionId(user)
    get("/logout", headers = Map("Cookie" -> s"sid=$sid"))
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should equal ("You have successfully logged out")
  }

  it should "fail to perform logout because there is no session cookie" in {
    get("/logout")
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should equal ("You are not logged in")
  }

  it should "should find out who is the user that logged in" in {
    val user = helper.getUser(randomRegLogin, randomRegPwd)
    val sid = helper.getSessionId(user)
    get("/api/v1/whoami", headers = Map("Cookie" -> s"sid=$sid"))
    val result = parseJson[WhoAmI](response.body)
    result.login.length should be > 0
  }

}

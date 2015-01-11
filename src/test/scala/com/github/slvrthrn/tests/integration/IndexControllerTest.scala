package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.IndexController
import com.github.slvrthrn.filters.IndexFilter
import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.entities.User
import com.twitter.finatra.FinatraServer
import com.twitter.finatra.test.FlatSpecHelper
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.scalatest.{Ignore, Matchers, BeforeAndAfterAll}
import scaldi.Injector

/**
 * Created by slvr on 1/9/15.
 */
//@Ignore
class IndexControllerTest extends IntegrationTest {

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomRegPwd = helper.randomRegPwd
    user = helper.registerUser(randomRegLogin, randomRegPwd)
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.clearCache
  }

  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomRegPwd: String = _
  var user: User = _

  override def server: FinatraServer = new TestServer {
    addFilter(new IndexFilter)
    register(new IndexController)
  }

  it should "show index page for authenticated user" in {
    val sid = helper.getSessionId(user)
    get("/", headers = Map("Cookie" -> s"sid=$sid"))
    response.body should include ("Hello world")
  }

  it should "not show index page for non-authenticated user" in {
    get("/")
    response.status should equal (HttpResponseStatus.FOUND)
    response.body should include ("Redirecting to auth")
  }

}

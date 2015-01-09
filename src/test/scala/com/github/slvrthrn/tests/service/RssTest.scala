package com.github.slvrthrn.tests.service

import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.mongodb.casbah.Imports._
import org.scalatest._

/**
 * Created by slvr on 1/8/15.
 */
//@Ignore
class RssTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  var helper: TestHelper = _
  var randomRegLogin: String = _
  var randomPwd: String = _
  var rssUrl: String = _

  override def beforeAll() = {
    helper = new TestHelper
    randomRegLogin = helper.randomRegLogin
    randomPwd = helper.randomRegPwd
    rssUrl = helper.getRssUrlStrFromConfig()
    helper.registerUser(randomRegLogin, randomPwd)
  }

  it should "insert new RSS URL successfully and then delete it with ObjectId" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val updatedUser = helper.insertRssUrl(rssUrl, user)
    updatedUser.isInstanceOf[User] should be (true)
    val url = helper.getRssUrl(rssUrl)
    val result = helper.deleteRssUrlFromUser(url, updatedUser)
    result should be (true)
  }

  it should "insert new RSS URL successfully again" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val result = helper.insertRssUrl(rssUrl, user)
    result.isInstanceOf[User] should be (true)
  }

  it should "download and parse news into the sequence" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val result = helper.getNews(user)
    result.size should be > 0
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.deleteRssUrl(rssUrl)
    helper.clearNewsCollection
    helper.clearCache
  }

}

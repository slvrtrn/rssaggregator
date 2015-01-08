package com.github.slvrthrn.tests.service

import com.github.slvrthrn.helpers.TestHelper
import com.github.slvrthrn.models.entities.RssUrl
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
    rssUrl = helper.getRssUrl
    helper.registerUser(randomRegLogin, randomPwd)
  }

  it should "insert new RSS URL successfully and then delete it with ObjectId" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val url = helper.insertRssUrl(rssUrl, user)
    url.isInstanceOf[RssUrl] should be (true)
    val updatedUser = helper.getUser(randomRegLogin, randomPwd)
    val result = helper.deleteRssUrlFromUser(url, updatedUser)
    result should be (true)
  }

  it should "insert new RSS URL successfully again" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val result = helper.insertRssUrl(rssUrl, user)
    result.isInstanceOf[RssUrl] should be (true)
  }

  it should "download and parse news into the sequence" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val result = helper.loadNews(user)
    result.size should be > 0
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.deleteRssUrl(rssUrl)
    helper.clearNewsCollection
    helper.clearCache
  }

}

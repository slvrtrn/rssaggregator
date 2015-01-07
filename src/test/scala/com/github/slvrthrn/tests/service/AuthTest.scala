package com.github.slvrthrn.tests.service

import com.github.slvrthrn.helpers.TestHelper
import org.bson.types.ObjectId
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

/**
 * Created by slvr on 1/8/15.
 */
class AuthTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  val helper = new TestHelper
  val randomRegLogin = helper.randomRegLogin
  val randomPwd = helper.randomRegPwd

  it should "register user successfully" in {
    val u = helper.registerUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal(randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Set[ObjectId]] should be (true)
    u.feed should be ('empty)
  }

  it should "process user login successfully" in {
    val u = helper.getUser(randomRegLogin, randomPwd)
    u._id.isInstanceOf[ObjectId] should be (true)
    u.login should equal (randomRegLogin)
    u.password should have length 60
    u.feed.isInstanceOf[Set[ObjectId]] should be (true)
  }

  it should "create session successfully" in {
    val user = helper.getUser(randomRegLogin, randomPwd)
    val sid = helper.getSessionId(user)
    sid should have length 36
  }

  override def afterAll() = {
    helper.deleteUser(randomRegLogin)
    helper.clearCache
  }

}

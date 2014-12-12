package com.github.slvrthrn.models.forms

import com.wix.accord.dsl._

/**
 * Created by slvr on 12/12/14.
 */
case class LoginForm(login: String, password: String)

object LoginForm {

  implicit val loginFormValidator = validator[ LoginForm ] { f =>
    f.login is notEmpty
    f.password is notEmpty
  }

}
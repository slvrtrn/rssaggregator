package com.github.slvrthrn.models.forms

import com.wix.accord.dsl._

/**
 * Created by slvr on 12/10/14.
 */
case class RegForm(login: String, password: String)

object RegForm {

  implicit val regFormValidator = validator[RegForm] { f =>
    f.login is notEmpty
    f.password is notEmpty
  }

}

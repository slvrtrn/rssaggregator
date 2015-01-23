package com.github.slvrthrn.views

/**
 * Created by slvr on 12/12/14.
 */
class AuthView extends View {

  val REG_TEMPLATE_NAME = "reg"
  val LOGIN_TEMPLATE_NAME = "login"

  def renderHtml(templateName: String) = {
    val template = getTemplate(templateName)
    renderTemplate(template)
  }

}

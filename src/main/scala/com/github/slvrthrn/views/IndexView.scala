package com.github.slvrthrn.views


/**
 * Created by slvr on 12/9/14.
 */
class IndexView extends View {

  val INDEX_TEMPLATE_NAME = "index"

  def renderHtml(templateName: String): String = {
    val template = getTemplate(templateName)
    renderTemplate(template)
  }

}

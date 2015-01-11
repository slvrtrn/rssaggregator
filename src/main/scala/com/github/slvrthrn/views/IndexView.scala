package com.github.slvrthrn.views


/**
 * Created by slvr on 12/9/14.
 */
class IndexView extends View {

  def renderHtml: String = {
    val template = getTemplate("index")
    renderTemplate(template)
  }

}

package com.github.slvrthrn.views

import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/12/14.
 */
class LoginView extends View {

  def renderHtml: String = {
    val template = Jade4J.getTemplate("./tpl/login.jade")
    Jade4J.render(template, model)
  }

}

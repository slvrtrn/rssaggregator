package com.github.slvrthrn.views

import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/12/14.
 */
class AuthView extends View {

  def renderHtml: String = {
    val template = getTemplate("auth")
    renderTemplate(template)
  }

}

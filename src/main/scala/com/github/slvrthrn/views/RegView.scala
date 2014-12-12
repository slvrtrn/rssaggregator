package com.github.slvrthrn.views

import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/12/14.
 */
class RegView {

  def renderHtml: String = {
    val template = Jade4J.getTemplate("./tpl/registration.jade")
    val model = Map[String, AnyRef]()
    Jade4J.render(template, model)
  }

}

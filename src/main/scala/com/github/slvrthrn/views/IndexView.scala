package com.github.slvrthrn.views

import java.io.File

import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/9/14.
 */
class IndexView {

  def renderHtml: String = {
    val template = Jade4J.getTemplate("./tpl/index.jade")
    val model = Map[String, AnyRef]()
    Jade4J.render(template, model)
  }

}

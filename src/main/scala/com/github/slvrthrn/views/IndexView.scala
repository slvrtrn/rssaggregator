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
    val list = ("First" -> IndexModel("value of first"), "Second" -> IndexModel("value of second"))
    val model: Map[String, AnyRef] = Map("something" -> list)
    Jade4J.render(template, model)
  }
}

case class IndexModel(title: String)

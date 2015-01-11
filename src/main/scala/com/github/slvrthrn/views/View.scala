package com.github.slvrthrn.views

import de.neuland.jade4j.template.JadeTemplate
import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/12/14.
 */
trait View {

  protected val model = scala.collection.mutable.Map[String, AnyRef]()

  def renderHtml: String

  def addToModel(key: String, entity: AnyRef): Unit = model += key -> entity

  protected def getTemplate(name: String): JadeTemplate = Jade4J.getTemplate(s"./frontend/tpl/$name.jade")

  protected def renderTemplate(template: JadeTemplate) = Jade4J.render(template, model)

}

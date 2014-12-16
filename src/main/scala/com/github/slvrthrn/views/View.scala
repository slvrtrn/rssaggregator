package com.github.slvrthrn.views

/**
 * Created by slvr on 12/12/14.
 */
trait View {

  val model = scala.collection.mutable.Map[String, AnyRef]()

  def renderHtml: String

  def addToModel(key: String, entity: AnyRef): Unit = {
    model += key -> entity
  }

}

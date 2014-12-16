package com.github.slvrthrn.views

import de.neuland.jade4j.Jade4J
import scala.collection.JavaConversions._

/**
 * Created by slvr on 12/9/14.
 */
class IndexView extends View {

  def renderHtml: String = {
    val template = Jade4J.getTemplate("./tpl/index.jade")
    Jade4J.render(template, model)
  }

}

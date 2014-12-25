package com.github.slvrthrn.controllers

import com.github.slvrthrn.views.IndexView
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class IndexController(implicit val inj: Injector) extends Controller with Injectable {

  get("/") { implicit request =>

    withUserContext { user =>

      val indexView = new IndexView
      indexView.addToModel("user", Map[String, String]("login" -> user.login))
      render.html(indexView.renderHtml).toFuture

    }

  }

}

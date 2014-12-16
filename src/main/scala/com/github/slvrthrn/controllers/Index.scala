package com.github.slvrthrn.controllers

import com.github.slvrthrn.views.IndexView
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class Index(implicit val inj: Injector) extends Controller with Injectable {

  get("/") { implicit request =>
    withUserContext { user =>
      val indexView = new IndexView
      indexView.addToModel("user", user)
      render.html(indexView.renderHtml).toFuture
    }
  }
}

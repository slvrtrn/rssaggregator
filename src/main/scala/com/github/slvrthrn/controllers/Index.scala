package com.github.slvrthrn.controllers

import com.github.slvrthrn.views.IndexView
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class Index(implicit val inj: Injector) extends Controller with Injectable {

  get("/") { request =>
    val indexView = new IndexView
    render.html(indexView.renderHtml).toFuture
  }
}

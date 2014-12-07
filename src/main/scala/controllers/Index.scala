package ru.slvr.controllers

import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class Index(implicit val inj: Injector) extends Controller with Injectable {

  get("/") { request =>
    render.plain("index").toFuture
  }
}

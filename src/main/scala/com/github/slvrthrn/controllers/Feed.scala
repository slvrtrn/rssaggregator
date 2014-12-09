package com.github.slvrthrn.controllers

import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class Feed(implicit val inj: Injector) extends Controller {
  get("/feed") { request =>
    render.plain("feed").toFuture
  }
}

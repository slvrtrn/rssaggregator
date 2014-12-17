package com.github.slvrthrn.controllers

import java.net.URL

import com.github.slvrthrn.services.RssService
import com.github.slvrthrn.views.IndexView
import com.mongodb.WriteResult
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class Index(implicit val inj: Injector) extends Controller with Injectable {

  get("/") { implicit request =>

    withUserContext { user =>

      val indexView = new IndexView
      indexView.addToModel("user", Map[String, String]("login" -> user.login))
      render.html(indexView.renderHtml).toFuture

    }

  }

//  post("/addrss") { implicit request =>
//
//    withUserContext { user =>
//
//      try {
//        val url = new URL(request.getParam("url"))
//        val rssService = inject[RssService]
//        rssService.addRssUrl(url, user)
//        render.plain("Rss is successfully added to collection").toFuture
//      } catch {
//        case e: java.net.MalformedURLException => render.plain("failed").toFuture
//      }
//    }
//
//  }

}

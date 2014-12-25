package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.services.RssService
import scaldi.Injector

/**
 * Created by slvr on 12/25/14.
 */
class NewsController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]

  get("/api/v1/news") { implicit request =>
    withUserContext { user =>
      val result = rssService.loadNews(user)
      result flatMap {
        case news: Seq[RssNews] => renderJsonArray(news)
      }
    }
  }

  get("/api/v1/news/:id") { implicit request =>
    withUserContext { user =>
      val param = request.routeParams.get("id")
      param match {
        case Some(id: String) =>
          val result = rssService.getNewsById(id)
          renderJson(result)
        case _ =>
          val errors = Seq(ErrorPayload(
            "There is no news item with specified ID",
            "News item was not found"
          ))
          renderJsonError(errors, 404)
      }
    }
  }

}

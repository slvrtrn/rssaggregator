package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.services.RssService
import com.typesafe.config.Config
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import scaldi.Injector

/**
 * Created by slvr on 12/25/14.
 */
class NewsController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]
  val config = inject[Config]

  val onPageLimit = config.getInt("app.default.news.onPageLimit")

  get("/api/v1/news") { implicit request =>
    withUserContext { user =>
      val result = rssService.getNews(user, onPageLimit)
      result flatMap renderJsonArray[RssNews]
    }
  }

  get("/api/v1/news/:id") { implicit request =>
    withUserContext { user =>
      withObjectIdParam { objectId =>
        val result = rssService.getNewsById(objectId)
        result flatMap {
          case Some(rssNews: RssNews) => renderJson(rssNews)
          case _ =>
            val errors = Seq(ErrorPayload(
              "There are no news with specified ID",
              "News item was not found"
            ))
            renderJsonError(errors, HttpResponseStatus.NOT_FOUND)
        }
      }
    }
  }

  get("/api/v1/news/start/:id") { implicit request =>
    withUserContext { user =>
      withObjectIdParam { objectId =>
        val result = rssService.getNewsWithRange(user.feed, objectId, onPageLimit)
        result flatMap renderJsonArray[RssNews]
      }
    }
  }

  get("/api/v1/news/url/:id") { implicit request =>
    withUserContext { user =>
      withObjectIdParam { objectId =>
        val result = rssService.getNewsByParent(objectId)
        result flatMap renderJsonArray[RssNews]
      }
    }

  }

}

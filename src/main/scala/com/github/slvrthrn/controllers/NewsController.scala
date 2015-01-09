package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.services.RssService
import org.bson.types.ObjectId
import scaldi.Injector

import scala.util.{Failure, Success, Try}

/**
 * Created by slvr on 12/25/14.
 */
class NewsController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]

  get("/api/v1/news") { implicit request =>
    withUserContext { user =>
      val result = rssService.getNews(user)
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
          val res = Try(new ObjectId(id))
          res match {
            case Success(objectId: ObjectId) =>
              val result = rssService.getNewsById(objectId)
              result flatMap {
                case Some(rssNews: RssNews) => renderJson(rssNews)
                case _ =>
                  val errors = Seq(ErrorPayload(
                    "There are no news with specified ID",
                    "News item was not found"
                  ))
                  renderJsonError(errors, 404)
              }
            case Failure(e) => renderBadRequest()
          }
        case _ => renderBadRequest()
      }
    }
  }

}

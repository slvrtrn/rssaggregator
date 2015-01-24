package com.github.slvrthrn.controllers

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.services.RssService
import com.typesafe.config.Config
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import scaldi.Injector

import scala.util.{Failure, Success, Try}

/**
 * Created by slvr on 12/25/14.
 */
class NewsController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]
  val config = inject[Config]

  val onPageLimit = config.getInt("app.default.news.onPageLimit")

  get("/api/v1/news") { implicit request =>
    withUserContext { user =>
      val start = Option(request.getParam("start")).filter(_.trim.nonEmpty)
      val result = start match {
        case Some(str: String) =>
          Try(new ObjectId(str)) match {
            case Success(startId: ObjectId) => rssService.getNewsWithRange(user.feed, startId, onPageLimit)
            case Failure(e) => rssService.getNews(user.feed, onPageLimit)
          }
        case _ => rssService.getNews(user.feed, onPageLimit)
      }
      result flatMap renderJsonArray[RssNews]
    }
  }

  get("/api/v1/urls/:id/news") { implicit request =>
    withUserContext { user =>
      withObjectIdParam { objectId =>
        if (user.feed contains objectId) {
          val start = Option(request.getParam("start")).filter(_.trim.nonEmpty)
          val result = start match {
            case Some(str: String) =>
              Try(new ObjectId(str)) match {
                case Success(startId: ObjectId) => rssService.getNewsWithRange(Set(objectId), startId, onPageLimit)
                case Failure(e) => rssService.getNews(Set(objectId), onPageLimit)
              }
            case _ => rssService.getNews(Set(objectId), onPageLimit)
          }
          result flatMap renderJsonArray[RssNews]
        } else {
          val errors = Seq(ErrorPayload(
            "Couldn't find subscription with specified ID",
            "Rss URL isn't in user's feed"
          ))
          renderJsonError(errors, HttpResponseStatus.NOT_FOUND)
        }
      }
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

}

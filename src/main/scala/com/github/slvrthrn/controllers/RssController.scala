package com.github.slvrthrn.controllers

import java.net.URL

import com.github.slvrthrn.models.dto.RssUrlDto
import com.github.slvrthrn.models.entities.RssUrl
import com.github.slvrthrn.services.{UserService, RssService}
import org.bson.types.ObjectId
import scaldi.Injector

import scala.util.{Success, Failure, Try}

/**
 * Created by slvr on 12/25/14.
 */
class RssController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]

  val userService = inject[UserService]

  get("/api/v1/urls") { implicit request =>
    withUserContext { user =>
      val feed = user.feed
      renderJson(feed)
    }
  }

  post ("/api/v1/urls") { implicit request =>
    withUserContext { user =>
      val urlParam = request.getParam("url")
      val url = new URL(urlParam)
      val result = rssService.addRssUrl(url, user)
      result flatMap {
        case Some(url: RssUrl) => renderJson(url)
        case _ =>
          val errors = Seq(ErrorPayload(
            "Specified URL is already in your RSS subscriptions list",
            "Duplicate RSS URL"
          ))
          renderJsonError(errors, 409)
      }
    }
  }

  delete ("/api/v1/urls/:id") { implicit request =>
    withUserContext { user =>
      val param = request.routeParams.get("id")
      param match {
        case Some(id: String) =>
          val result = rssService.removeRssUrl(id, user)
          renderJson(result)
        case _ =>
          val errors = Seq(ErrorPayload(
            "Specified URL wasn't found in your subscriptions list",
            "RSS URL wasn't found"
          ))
          renderJsonError(errors, 404)
      }
    }
  }

}

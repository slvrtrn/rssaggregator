package com.github.slvrthrn.controllers

import java.net.URL

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.services.{UserService, RssService}
import org.bson.types.ObjectId
import scaldi.Injector

import scala.util.{Failure, Success, Try}

/**
 * Created by slvr on 12/25/14.
 */
class RssController(implicit val inj: Injector) extends Controller {

  val rssService = inject[RssService]

  val userService = inject[UserService]

  get("/api/v1/urls") { implicit request =>
    withUserContext { user =>
      val result = rssService.findRssUrlByUser(user)
      result flatMap {
        case seq: Seq[RssUrl] => renderJsonArray(seq)
      }
    }
  }

  post ("/api/v1/urls") { implicit request =>
    withUserContext { user =>
      val urlOpt = parseJsonRequest[String](request)
      urlOpt match {
        case Some(str: String) =>
          val url = new URL(str)
          val result = rssService.addRssUrl(url, user)
          result flatMap {
            case user: User => renderJson(user.feed)
            case _ =>
              val errors = Seq(ErrorPayload(
                "Specified URL is already in your RSS subscriptions list",
                "Duplicate RSS URL"
              ))
              renderJsonError(errors, 409)
          }
        case _ =>
          val errors = Seq(ErrorPayload(
            "Invalid URL format",
            "Cannot parse URL JSON"
          ))
          renderJsonError(errors, 400)
      }
    }
  }

  delete ("/api/v1/urls/:id") { implicit request =>
    withUserContext { user =>
      val param = request.routeParams.get("id")
      param match {
        case Some(id: String) =>
          val res = Try(new ObjectId(id))
          res match {
            case Success(objectId: ObjectId) =>
              val result = rssService.removeRssUrl(objectId, user)
              result flatMap {
                case true => renderJson(result)
                case false =>
                  val errors = Seq(ErrorPayload(
                    "Specified URL wasn't found in your subscriptions list",
                    "RSS URL wasn't found"
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

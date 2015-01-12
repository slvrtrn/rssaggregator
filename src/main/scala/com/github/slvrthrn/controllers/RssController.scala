package com.github.slvrthrn.controllers

import java.net.URL

import com.github.slvrthrn.models.dto.RssUrlDto
import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.services.{UserService, RssService}
import org.bson.types.ObjectId
import org.jboss.netty.handler.codec.http.HttpResponseStatus
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
      val result = rssService.findRssUrlsByUser(user)
      result flatMap {
        case seq: Seq[RssUrl] => renderJsonArray(seq)
      }
    }
  }

  post ("/api/v1/urls") { implicit request =>
    withUserContext { user =>
      val urlOpt = parseJsonRequest[RssUrlDto](request)
      urlOpt match {
        case Some(dto: RssUrlDto) =>
          Try(new URL(dto.url)) match {
            case Success(url: URL) =>
              val result = rssService.addRssUrl(url, user)
              result flatMap {
                updatedUser: User =>
                  if(updatedUser.feed equals user.feed) {
                    val errors = Seq(ErrorPayload(
                      "Specified URL is already in your RSS subscriptions list",
                      "Duplicate RSS URL"
                    ))
                    renderJsonError(errors, HttpResponseStatus.CONFLICT)
                  } else {
                    val feed = rssService.findRssUrlsByUser(updatedUser)
                    feed flatMap renderJson[Seq[RssUrl]]
                  }
              }
            case Failure(e) => renderInvalidUrlFormat
          }
        case _ => renderInvalidUrlFormat
      }
    }
  }

  delete ("/api/v1/urls/:id") { implicit request =>
    withUserContext { user =>
      withObjectIdParam { objectId =>
        val result = rssService.removeRssUrl(objectId, user)
        result flatMap {
          case true => renderJson(result)
          case false =>
            val errors = Seq(ErrorPayload(
              "Specified URL wasn't found in your subscriptions list",
              "RSS URL wasn't found"
            ))
            renderJsonError(errors, HttpResponseStatus.NOT_FOUND)
        }
      }
    }
  }

  private def renderInvalidUrlFormat = {
    val errors = Seq(ErrorPayload(
      "Invalid URL format",
      "Cannot parse URL from JSON"
    ))
    renderJsonError(errors, HttpResponseStatus.BAD_REQUEST)
  }

}

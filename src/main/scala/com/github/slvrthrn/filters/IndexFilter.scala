package com.github.slvrthrn.filters

import java.util.concurrent.TimeUnit
import com.github.slvrthrn.models.entities.Session
import com.github.slvrthrn.services.SessionService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse, Cookie}
import com.twitter.app.App
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.{Duration, Future}
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import scaldi.Injector
import com.twitter.finatra.{Request => FinatraRequest}

/**
 * Created by slvr on 12/12/14.
 */
class IndexFilter(implicit val inj: Injector)
  extends SimpleFilter[FinagleRequest, FinagleResponse] with App with InjectHelper {

  def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]): Future[FinagleResponse] = {
    request.uri match {
      case "/reg" | "/login" => processAuthRequest(request, service)
      case uri: String if uri.startsWith("/app/") => redirectToIndex(request, service)
      case uri: String if uri.contains("/api/v1/") | uri == "/" => processUserRequest(request, service)
      case _ => service(request)
    }
  }

  private def processAuthRequest(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse])
  : Future[FinagleResponse] = {
    val sessionService = inject[SessionService]
    request.cookies.get("sid") match {
      case Some(c: Cookie) =>
        sessionService.getSession(c.value) flatMap {
          case Some(s: Session) => redirectToIndex(request, service, "Already logged in, redirecting to index")
          case _ => service(request)
        }
      case _ => service(request)
    }
  }

  private def processUserRequest(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]) = {
    val sessionService = inject[SessionService]
    request.cookies.get("sid") match {
      case Some(c: Cookie) =>
        sessionService.getSession(c.value) flatMap {
          case Some(s: Session) => service(UserRequest(request, s))
          case _ =>
            val expired = new Cookie("sid", "")
            expired.maxAge = Duration(-42, TimeUnit.DAYS)
            Future value new ResponseBuilder()
              .plain("Redirecting to auth").status(HttpResponseStatus.FOUND.getCode)
              .header("Location", "/login").cookie(expired).build
        }
      case _ =>
        Future value new ResponseBuilder()
          .plain("Redirecting to auth").status(HttpResponseStatus.FOUND.getCode)
          .header("Location", "/login").build
    }
  }

  private def redirectToIndex(request: FinagleRequest,
                              service: Service[FinagleRequest, FinagleResponse],
                               msg: String = "Redirecting to index")
  : Future[FinagleResponse] = {
    Future value new ResponseBuilder()
      .plain(msg).status(HttpResponseStatus.FOUND.getCode)
      .header("Location", "/").build
  }
}

case class UserRequest(override val request: FinagleRequest, session: Session) extends FinatraRequest(request)

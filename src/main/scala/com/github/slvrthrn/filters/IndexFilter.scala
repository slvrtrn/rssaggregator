package com.github.slvrthrn.filters

import java.util.concurrent.TimeUnit
import com.github.slvrthrn.models.Session
import com.github.slvrthrn.services.SessionService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse, Cookie}
import com.twitter.app.App
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.{Duration, Future}
import scaldi.Injector
import com.twitter.finatra.{Request => FinatraRequest}

/**
 * Created by slvr on 12/12/14.
 */
class IndexFilter(implicit val inj: Injector)
  extends SimpleFilter[FinagleRequest, FinagleResponse] with App with InjectHelper  {

  def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]) = {

    val sessionService = inject[SessionService]

    request.cookies.get("sid") match {
      case Some(c: Cookie) =>
        sessionService.getSession(c.value) flatMap {
          case Some(s: Session) => service(AuthRequest(request, s))
          case _ =>
            if (!request.uri.contains("/login")) {
              val expired = new Cookie("sid", "")
              expired.maxAge = Duration(-42, TimeUnit.DAYS)
              Future value new ResponseBuilder()
                .plain("Redirecting to login").status(302).header("Location", "/login").cookie(expired).build
            } else service(request)
        }
      case _ =>
        if (!request.uri.contains("/login")) {
          Future value new ResponseBuilder()
            .plain("Redirecting to login").status(302).header("Location", "/login").build
        } else service(request)
    }

  }

}

case class AuthRequest(override val request: FinagleRequest, session: Session) extends FinatraRequest(request)

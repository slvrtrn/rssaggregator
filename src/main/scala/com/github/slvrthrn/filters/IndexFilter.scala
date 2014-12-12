package com.github.slvrthrn.filters

import java.util.concurrent.TimeUnit
import com.github.slvrthrn.services.SessionService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse, Cookie}
import com.twitter.app.App
import com.twitter.finatra.ResponseBuilder
import com.twitter.util.{Duration, Future}
import scaldi.Injector

/**
 * Created by slvr on 12/12/14.
 */
class IndexFilter(implicit val inj: Injector)
  extends SimpleFilter[FinagleRequest, FinagleResponse] with App with InjectHelper  {

  def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]) = {

//    val sessionService = inject[SessionService]
//
//    request.cookies.get("sid") match {
//      case Some(c: Cookie) =>
//        sessionService.checkSession(c.value) flatMap {
//          case true => service(request)
//          case _ => if (request.uri != "/login") redirectToLogin else service(request)
//        }
//      case None => redirectToLogin
//    }
    service(request)
  }

//  def redirectToLogin = {
//    val expired = new Cookie("sid", "")
//    expired.maxAge = Duration(-10, TimeUnit.DAYS)
//    Future value new ResponseBuilder().plain("Redirecting to login").status(302).header("Location", "/login").cookie(expired).build
//  }
}

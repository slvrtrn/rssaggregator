package com.github.slvrthrn.tests.integration

import com.github.slvrthrn.filters.{IndexFilter, UserRequest}
import com.github.slvrthrn.models.entities.{User, Session}
import com.github.slvrthrn.services.SessionService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request => Req, Response => Res}
import com.twitter.finatra.FinatraServer
import com.twitter.util.Future
import scaldi.Injector
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.github.slvrthrn.utils.Twitter._

/**
* Created by slvr on 1/8/15.
*/
class TestServer(implicit val inj: Injector) extends FinatraServer with InjectHelper{

  private val awaitTimeout = Duration.Inf

  private val sessionService = inject[SessionService]

  def dropFilters(){
    filters = Seq()
  }

  def withUserContext(user: User): Unit = {
    val futureSid = sessionService.createSession(user).asScala
    val sid = Await.result(futureSid, awaitTimeout).get
    addFilter(new SimpleFilter[Req, Res]{
      override def apply(request: Req, service: Service[Req, Res]): Future[Res] = {
        service.apply(new UserRequest(request, Session(id = sid, uid = user._id)))
      }
    })
  }

}
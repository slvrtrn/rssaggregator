package com.github.slvrthrn.services.impl

import java.util.UUID

import com.github.slvrthrn.models.{Session, User}
import com.github.slvrthrn.repositories.cache.SessionCache
import com.github.slvrthrn.services.SessionService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.util.Future
import scaldi.Injector
import com.github.slvrthrn.utils.Twitter._

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/11/14.
 */
class SessionServiceImpl (implicit val inj: Injector) extends SessionService with InjectHelper {

  private val sessionCache = inject[SessionCache]

  private implicit val executionContext = inject[ExecutionContext]

  def checkSession(sid: String): Future[Boolean] = {
    sessionCache.get(sid).asTwitter map {
      case Some(s: String) => true
      case _ => false
    }
  }

  def createSession(u: User): Future[Option[String]] = {
    val id = UUID.randomUUID.toString
    val session = Session(id, u._id)
    sessionCache.put(session).asTwitter map {
      case true => Some(id)
      case _ => None
    }
  }

}

package com.github.slvrthrn.services

import com.github.slvrthrn.models.{Session, User}
import com.twitter.util.Future

/**
 * Created by slvr on 12/11/14.
 */
trait SessionService {

  def getSession(sid: String): Future[Option[Session]]

  def createSession(u: User): Future[Option[String]]

}

package com.github.slvrthrn.services

import com.github.slvrthrn.models.User
import com.twitter.util.Future

/**
 * Created by slvr on 12/11/14.
 */
trait SessionService {

  def checkSession(sid: String): Future[Boolean]

  def createSession(u: User): Future[Option[String]]

}

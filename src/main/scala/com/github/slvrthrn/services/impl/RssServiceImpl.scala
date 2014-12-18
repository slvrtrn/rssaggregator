package com.github.slvrthrn.services.impl

import java.net.URL

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.repositories.{UserRepo, RssUrlRepo, RssNewsRepo}
import com.github.slvrthrn.services.RssService
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.commons.MongoDBObject
import com.twitter.util.Future
import scaldi.Injector
import com.mongodb.casbah.Imports._

import scala.concurrent.ExecutionContext

/**
 * Created by slvr on 12/17/14.
 */
class RssServiceImpl (implicit val inj: Injector) extends RssService with InjectHelper {

  val newsRepo = inject[RssNewsRepo]

  val urlRepo = inject[RssUrlRepo]

  val userRepo = inject[UserRepo]

  private implicit val executionContext = inject[ExecutionContext]

  def addRssUrl(url: URL, user: User): Future[WriteResult] = {
    val urlStr  = url.toString
    userRepo.save(user.copy(feed = user.feed + urlStr))
    urlRepo.update(MongoDBObject("url" -> urlStr), RssUrl(urlStr), true)
  }

}

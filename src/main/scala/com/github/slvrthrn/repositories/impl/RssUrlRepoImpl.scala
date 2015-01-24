package com.github.slvrthrn.repositories.impl

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.repositories.RssUrlRepo
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.util.Future
import scaldi.Injector
import com.mongodb.casbah.Imports._

/**
 * Created by slvr on 12/17/14.
 */
class RssUrlRepoImpl(implicit val inj: Injector) extends RssUrlRepo with InjectHelper {

  def findByUrl(url: String): Future[Option[RssUrl]] = findOne("url" $eq url)

  def findByFeed(feed: Set[ObjectId]): Future[Seq[RssUrl]] = find("_id" $in feed)

}

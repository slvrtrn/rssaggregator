package com.github.slvrthrn.repositories.impl

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.repositories.RssNewsRepo
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.util.Future
import org.bson.types.ObjectId
import scaldi.Injector
import com.mongodb.casbah.Imports._

/**
 * Created by slvr on 12/17/14.
 */
class RssNewsRepoImpl(implicit val inj: Injector) extends RssNewsRepo with InjectHelper {

  def findByParent(parent: ObjectId, limit: Int = 0): Future[Seq[RssNews]] = find("parent" $eq parent, limit)

  def removeByParent(parent: ObjectId): Future[Boolean] = removeBy("parent" $eq parent)

  def findByFeed(feed: Set[ObjectId], limit: Int): Future[Seq[RssNews]] = {
    find("parent" $in feed, limit, MongoDBObject("pubDate" -> -1))
  }

}

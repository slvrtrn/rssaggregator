package com.github.slvrthrn.repositories

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.utils.InjectHelper
import com.novus.salat.dao.SalatDAO
import com.twitter.util.Future
import org.bson.types.ObjectId
import com.novus.salat.global.ctx

/**
 * Created by slvr on 12/17/14.
 */
trait RssUrlRepo extends MongoDaoRepository[RssUrl] { self: InjectHelper =>

  protected val collection = db("rssUrl")

  protected val dao = new SalatDAO[RssUrl, ObjectId](collection = collection) {}

  def findByUrl(url: String): Future[Option[RssUrl]]

  def findByFeed(feed: Set[ObjectId]): Future[Seq[RssUrl]]

}

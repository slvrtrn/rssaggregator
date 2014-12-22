package com.github.slvrthrn.repositories

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.utils.InjectHelper
import com.novus.salat.dao.SalatDAO
import com.twitter.util.Future
import org.bson.types.ObjectId
import com.novus.salat.global._

/**
 * Created by slvr on 12/17/14.
 */
trait RssNewsRepo extends MongoDaoRepository[RssNews] { self: InjectHelper =>

  protected val collection = db("rssNews")

  protected val dao = new SalatDAO[RssNews, ObjectId](collection = collection) {}

  def findByParent(parent: ObjectId): Future[Seq[RssNews]]

  def removeByParent(parent: ObjectId): Future[Boolean]

}
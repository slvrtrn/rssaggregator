package com.github.slvrthrn.repositories

import com.github.slvrthrn.models.entities.RssUrl
import com.github.slvrthrn.utils.InjectHelper
import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import com.novus.salat.global._

/**
 * Created by slvr on 12/17/14.
 */
trait RssUrlRepo extends MongoDaoRepository[RssUrl] { self: InjectHelper =>

  protected val collection = db("rssUrl")

  protected val dao = new SalatDAO[RssUrl, ObjectId](collection = collection) {}
}

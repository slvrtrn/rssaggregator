package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.repositories.impl.RssNewsRepoImpl
import com.twitter.util.Future
import org.bson.types.ObjectId
import scaldi.Injector

/**
 * Created by slvr on 12/21/14.
 */
class RssNewsCache(implicit inj: Injector) extends RssNewsRepoImpl with Cache {

  val rssNewsParentCache = cacheFor[Seq[RssNews]]("rssNewsParent")

  override def findByParent(parent: ObjectId): Future[Seq[RssNews]] = {
    rssNewsParentCache.getOrElseUpdate(parent) {
      super.findByParent(parent)
    }
  }

  override def removeByParent(parent: ObjectId): Future[Boolean] = {
    super.removeByParent(parent) onSuccess {
      case result: Boolean => if (result) rssNewsParentCache.evict(parent)
    }
  }

  override def saveTraversable(seq: Traversable[RssNews]): Future[Traversable[RssNews]] = {
    super.saveTraversable(seq) onSuccess {
      case news: Seq[RssNews] =>
        if(news.nonEmpty) rssNewsParentCache.save(news.head.parent, news)
    }
  }

}

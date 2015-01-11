package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.repositories.impl.RssUrlRepoImpl
import com.twitter.util.Future
import org.bson.types.ObjectId
import scaldi.Injector

/**
 * Created by slvr on 12/22/14.
 */
class RssUrlCache(implicit inj: Injector) extends RssUrlRepoImpl with Cache {

  val rssUrlSeqCache = cacheFor[Seq[RssUrl]]("rssUrlSeqByUserId")
  val rssUrlOptionUrlCache = cacheFor[Option[RssUrl]]("rssUrlOptionByUrl")
  val rssUrlOptionIdCache = cacheFor[Option[RssUrl]]("rssUrlOptionById")

  override def findByUrl(url: String): Future[Option[RssUrl]] = {
    rssUrlOptionUrlCache.getOrElseUpdate(url) {
      super.findByUrl(url)
    }
  }

  override def findByUser(user: User): Future[Seq[RssUrl]] = {
    rssUrlSeqCache.getOrElseUpdate(user._id) {
      super.findByUser(user)
    }
  }

  override def findById(id: ObjectId): Future[Option[RssUrl]] = {
    rssUrlOptionIdCache.getOrElseUpdate(id) {
      super.findById(id)
    }
  }

  override def save(url: RssUrl): Future[RssUrl] = {
    super.save(url) onSuccess {
      case rss: RssUrl =>
        rssUrlOptionIdCache.evict(rss._id)
        rssUrlOptionUrlCache.save(rss.url, Some(rss))
    }
  }

}

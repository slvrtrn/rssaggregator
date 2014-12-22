package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.entities.{User, RssUrl}
import com.github.slvrthrn.repositories.impl.RssUrlRepoImpl
import com.twitter.util.Future
import scaldi.Injector

/**
 * Created by slvr on 12/22/14.
 */
class RssUrlCache(implicit inj: Injector) extends RssUrlRepoImpl with Cache {

  val rssUrlSeqCache = cacheFor[Seq[RssUrl]]("rssUrlSeq")

  val rssUrlOptionCache = cacheFor[Option[RssUrl]]("rssUrlOption")

  override def findByUrl(url: String): Future[Option[RssUrl]] = {
    rssUrlOptionCache.getOrElseUpdate(url) {
      super.findByUrl(url)
    }
  }

  override def findByUser(user: User): Future[Seq[RssUrl]] = {
    rssUrlSeqCache.getOrElseUpdate(user._id) {
      super.findByUser(user)
    }
  }

  override def save(url: RssUrl): Future[RssUrl] = {
    super.save(url) onSuccess {
      case rss: RssUrl => rssUrlOptionCache.save(rss.url, Some(rss))
    }
  }

}

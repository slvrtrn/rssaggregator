package com.github.slvrthrn.repositories.cache

import com.github.slvrthrn.models.entities.RssNews
import com.github.slvrthrn.repositories.impl.RssNewsRepoImpl
import com.twitter.util.Future
import scaldi.Injector

/**
 * Created by slvr on 12/21/14.
 */
class RssNewsCache(implicit inj: Injector) extends RssNewsRepoImpl with Cache {

  val rssNewsParentCache = cacheFor[Seq[RssNews]]("rssNews")

}

package com.github.slvrthrn.services

import java.net.URL

import com.github.slvrthrn.models.entities.{RssNews, User}
import com.twitter.util.Future

/**
 * Created by slvr on 12/17/14.
 */
trait RssService {

  def addRssUrl(url: URL, user: User): Future[Boolean]

  def loadNews(user: User): Future[List[RssNews]]

}

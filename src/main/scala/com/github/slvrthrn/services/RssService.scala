package com.github.slvrthrn.services

import java.net.URL

import com.github.slvrthrn.models.entities.{RssUrl, RssNews, User}
import com.twitter.util.Future

/**
 * Created by slvr on 12/17/14.
 */
trait RssService {

  def addRssUrl(url: URL, user: User): Future[Option[RssUrl]]

  def removeRssUrl(id: String, user: User): Future[Boolean]

  def loadNews(user: User): Future[Seq[RssNews]]

  def getNewsById(id: String): Future[Option[RssNews]]

}

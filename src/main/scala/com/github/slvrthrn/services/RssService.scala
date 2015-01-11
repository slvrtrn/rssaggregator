package com.github.slvrthrn.services

import java.net.URL

import com.github.slvrthrn.models.entities.{RssUrl, RssNews, User}
import com.mongodb.casbah.Imports._
import com.twitter.util.Future
import org.bson.types.ObjectId

/**
 * Created by slvr on 12/17/14.
 */
trait RssService {

  def addRssUrl(url: URL, user: User): Future[User]
  def removeRssUrl(id: ObjectId, user: User): Future[Boolean]
  def getNews(user: User, onPageLimit: Int): Future[Seq[RssNews]]
  def getNewsById(id: ObjectId): Future[Option[RssNews]]
  def findRssUrlByUser(user: User): Future[Seq[RssUrl]]
  def getNewsWithRange(feed: Set[ObjectId], startFrom: ObjectId, limit: Int): Future[Seq[RssNews]]
  def getNewsByParent(parent: ObjectId, limit: Int = 0): Future[Seq[RssNews]]

}

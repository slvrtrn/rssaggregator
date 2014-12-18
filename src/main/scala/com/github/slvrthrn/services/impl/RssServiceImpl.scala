package com.github.slvrthrn.services.impl

import java.net.URL

import com.github.slvrthrn.models.entities.{RssNews, User, RssUrl}
import com.github.slvrthrn.repositories.{UserRepo, RssUrlRepo, RssNewsRepo}
import com.github.slvrthrn.services.RssService
import com.github.slvrthrn.utils.InjectHelper
import com.mongodb.casbah.commons.MongoDBObject
import com.twitter.util.Future
import scaldi.Injector
import com.mongodb.casbah.Imports._

import scala.concurrent.ExecutionContext
import scala.xml.{Node, XML}

/**
 * Created by slvr on 12/17/14.
 */
class RssServiceImpl (implicit val inj: Injector) extends RssService with InjectHelper {

  val newsRepo = inject[RssNewsRepo]

  val urlRepo = inject[RssUrlRepo]

  val userRepo = inject[UserRepo]

  private implicit val executionContext = inject[ExecutionContext]

  def addRssUrl(url: URL, user: User): Future[Boolean] = {
    val urlStr  = url.toString
    val result = urlRepo.update(MongoDBObject("url" -> urlStr), RssUrl(urlStr), upsert = true)
    result map {
      case wr: WriteResult =>
        val id = wr.getUpsertedId.asInstanceOf[ObjectId].toString
        userRepo.save(user.copy(feed = user.feed.updated(id, urlStr)))
        true
      case _ => false
    }
  }

  def loadNews(user: User): Future[List[RssNews]] = Future {
    var news = List[RssNews]()
    user.feed foreach {
      case (id, url) => 
        news :::= (XML.load(url) \\ "item").map(buildNews(_, id)).toList
    }
    news
  }

  def buildNews(node: Node, id: String): RssNews = new RssNews(
    title = (node \\ "title").text,
    link = (node \\ "link").text,
    description = (node \\ "description").text,
    parent = id
  )

}

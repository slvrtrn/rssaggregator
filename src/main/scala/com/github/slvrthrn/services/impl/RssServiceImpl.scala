package com.github.slvrthrn.services.impl

import java.net.URL

import com.github.slvrthrn.models.entities.{RssNews, User, RssUrl}
import com.github.slvrthrn.repositories.{UserRepo, RssUrlRepo, RssNewsRepo}
import com.github.slvrthrn.services.RssService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.util.Future
import org.joda.time.{Seconds, DateTime}
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

  def addRssUrl(url: URL, user: User): Future[Option[RssUrl]] = {
    val urlStr  = url.toString
    val checkDb = checkRssUrlExitestence(urlStr)
    checkDb map {
      case Some(rssUrl: RssUrl) =>
        val checkFeed = user.feed contains rssUrl._id
        checkFeed match {
          case true => None
          case false =>
            userRepo.save(user.copy(feed = user.feed + rssUrl._id))
            Some(rssUrl)
        }
      case _ =>
        val rssUrl = RssUrl(urlStr)
        userRepo.save(user.copy(feed = user.feed + rssUrl._id))
        urlRepo.save(rssUrl)
        Some(rssUrl)
    }
  }

//  def removeRssUrl(id: String): Future[Boolean] = {
//    val rssUrl = urlRepo.findById(new ObjectId(id))
//    rssUrl flatMap {
//      case Some(url: RssUrl) => urlRepo.remove(url)
//      case _ => Future value false
//    }
//  }

  def removeRssUrl(id: String, user: User): Future[Boolean] = {
    val objectId = new ObjectId(id)
    val newFeed = user.feed.filterNot(_ == objectId)
    newFeed equals user.feed match {
      case true => Future value false
      case false =>
        userRepo.save(user.copy(feed = newFeed))
        Future value true
    }
  }

  def loadNews(user: User): Future[Seq[RssNews]] = {
    val urls = urlRepo.findByUser(user)
    val now = new DateTime
    for {
      news <- urls flatMap ( urlSeq => {
        val result = urlSeq map ( rssUrl => {
            if (Seconds.secondsBetween(rssUrl.lastUpdate, now).getSeconds > 60)
              Future {
                val res = (XML.load(rssUrl.url) \\ "item").map(buildNews(_, rssUrl._id)).toSeq
                urlRepo.save(rssUrl.copy(lastUpdate = now))
                newsRepo.removeByParent(rssUrl._id)
                newsRepo.saveTraversable(res)
                res
              }
            else Future value Seq()
          })
        Future.collect(result).map(_.flatten)
      })
    } yield news
  }

  def getNewsById(id: String): Future[Option[RssNews]] = newsRepo.findById(new ObjectId(id))

  private def buildNews(node: Node, id: ObjectId): RssNews = new RssNews(
    title = (node \\ "title").text,
    link = (node \\ "link").text,
    description = (node \\ "description").text,
    parent = id
  )

  private def checkRssUrlExitestence(url: String): Future[Option[RssUrl]] = {
    urlRepo.findByUrl(url) map {
      case Some(url: RssUrl) => Some(url)
      case _ => None
    }
  }

}

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
import scala.util.{Failure, Success, Try}
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

  def getNewsById(id: String): Future[Option[RssNews]] = newsRepo.findById(new ObjectId(id))

  def getNews(user: User): Future[Seq[RssNews]] = {
    val now = new DateTime
    for {
      urls <- urlRepo.findByUser(user)
      news <- (Future collect urls.map { rssUrl =>
        if (Seconds.secondsBetween(rssUrl.lastUpdate, now).getSeconds > 60)
          loadNews(rssUrl)
        else
          newsRepo.findByParent(rssUrl._id)
      }).map(_.flatten)
    } yield news
  }

  private def loadNews(rssUrl: RssUrl): Future[Seq[RssNews]] = {
    Future {
      Try(XML.load(rssUrl.url))
    }.flatMap {
      case Success(xml) =>
        val freshNews = (xml \\ "items").map(buildNews(_, rssUrl._id)).toSeq
        saveNewsSeq(freshNews, rssUrl)
      case Failure(e) => newsRepo.findByParent(rssUrl._id)
    }
  }

  private def saveNewsSeq(freshNews: Seq[RssNews], rssUrl: RssUrl): Future[Seq[RssNews]] = {
    for {
      _ <- newsRepo.removeByParent(rssUrl._id)
      _ <- urlRepo.save(rssUrl.copy(lastUpdate = new DateTime))
      news <- newsRepo.saveTraversable(freshNews)
    } yield news.toSeq // ???
  }

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

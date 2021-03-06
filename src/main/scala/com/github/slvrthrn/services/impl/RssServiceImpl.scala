package com.github.slvrthrn.services.impl

import java.net.URL

import com.github.slvrthrn.models.entities._
import com.github.slvrthrn.repositories.{UserRepo, RssUrlRepo, RssNewsRepo}
import com.github.slvrthrn.services.RssService
import com.github.slvrthrn.utils.InjectHelper
import com.twitter.util.Future
import com.typesafe.config.Config
import org.bson.types
import org.joda.time.format.DateTimeFormat
import org.joda.time.{Seconds, DateTime}
import scaldi.Injector
import com.mongodb.casbah.Imports._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import scala.xml._

/**
 * Created by slvr on 12/17/14.
 */
class RssServiceImpl (implicit val inj: Injector) extends RssService with InjectHelper {

  private val newsRepo = inject[RssNewsRepo]

  private val urlRepo = inject[RssUrlRepo]

  private val userRepo = inject[UserRepo]

  private val formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")

  private val config = inject[Config]

  private val minRefreshInterval = config.getInt("app.default.news.minRefreshInterval")

  private val xmlFetchTimeout = config.getInt("app.default.news.xmlFetchTimeout")

  private implicit val executionContext = inject[ExecutionContext]

  def addRssUrl(url: URL, user: User, xml: Elem): Future[User] = {
    val urlStr  = url.toString
    val checkDb = checkRssUrlExitestence(urlStr)
    checkDb flatMap {
      case Some(rssUrl: RssUrl) =>
        val checkFeed = user.feed contains rssUrl._id
        checkFeed match {
          case true => Future value user
          case false =>
            val updatedUser = user.copy(feed = user.feed + rssUrl._id)
            userRepo.save(updatedUser)
        }
      case _ =>
        val imageLink = (xml \ "channel" \ "image" \ "url").text
        val imageOpt = if(imageLink.nonEmpty) {
          Some(imageLink)
        } else {
          None
        }
        val rssUrl = RssUrl(urlStr, image = imageOpt)
        val updatedUser = user.copy(feed = user.feed + rssUrl._id)
        for {
          _ <- urlRepo.save(rssUrl)
          savedUser <- userRepo.save(updatedUser)
        } yield savedUser
    }
  }

  def checkRssUrlSanity(url: URL): Future[Option[Elem]] = {
    Future {
      Try {
        val connection = url.openConnection
        connection.setConnectTimeout(xmlFetchTimeout)
        connection.setReadTimeout(xmlFetchTimeout)
        XML.load(connection.getInputStream)
      }
    }.map {
      case Success(xml) =>
        val title = (xml \ "channel" \ "title").text
        val desc = (xml \ "channel" \ "description").text
        val link = (xml \ "channel" \ "link").text
        if (title.nonEmpty && desc.nonEmpty && link.nonEmpty) {
          Some(xml)
        } else {
          None
        }
      case Failure(e) => None
    }
  }

  def removeRssUrl(id: ObjectId, user: User): Future[Boolean] = {
    val newFeed = user.feed.filterNot(_ == id)
    newFeed equals user.feed match {
      case true => Future value false
      case false =>
        for {
          _ <- userRepo.save(user.copy(feed = newFeed))
        } yield true
    }
  }

  def findRssUrlsByUser(feed: Set[ObjectId]): Future[Seq[RssUrl]] = urlRepo.findByFeed(feed)

  def getNewsById(id: ObjectId): Future[Option[RssNews]] = newsRepo.findById(id)

  def getNews(feed: Set[ObjectId], limit: Int): Future[Seq[RssNews]] = {
    val now = new DateTime
    for {
      urls <- urlRepo.findByFeed(feed)
      _ <- {
        val feedToUpdate = urls.filter(
          rssUrl => Seconds.secondsBetween(rssUrl.lastUpdate, now).getSeconds > minRefreshInterval
        )
        Future collect feedToUpdate.map(loadNews)
      }
      news <- newsRepo.findByFeed(feed, limit)
    } yield news
  }

  def getNewsWithRange(feed: Set[ObjectId], startFrom: ObjectId, limit: Int): Future[Seq[RssNews]] = {
    newsRepo.findAllWithRange("parent" $in feed, startFrom, limit)
  }

  private def loadNews(rssUrl: RssUrl): Future[Seq[RssNews]] = {
    Future {
      Try {
        val connection = new URL(rssUrl.url).openConnection
        connection.setConnectTimeout(xmlFetchTimeout)
        connection.setReadTimeout(xmlFetchTimeout)
        XML.load(connection.getInputStream)
      }
    }.flatMap {
      case Success(xml) =>
        val freshNews = (xml \\ "item").map(buildNews(_, rssUrl._id)).toSeq
        saveNewsSeq(freshNews, rssUrl)
      case Failure(e) => Future value Seq[RssNews]()
    }
  }

  private def saveNewsSeq(freshNews: Seq[RssNews], rssUrl: RssUrl): Future[Seq[RssNews]] = {
    for {
      oldNews <- newsRepo.findByParent(rssUrl._id, limit = freshNews.size)
      _ <- urlRepo.save(rssUrl.copy(lastUpdate = new DateTime))
      news <- {
        val diff = freshNews filterNot(item => oldNews.exists(_.title == item.title))
        newsRepo.saveTraversable(diff)
      }
    } yield news.toSeq // ???
  }

  private def buildNews(node: Node, id: ObjectId): RssNews = {
    val encNode = node \\ "enclosure"
    val enclosure = encNode match {
      case nodeSeq: NodeSeq if nodeSeq.nonEmpty =>
        val url = (encNode \ "@url").text
        val mime = (encNode \ "@type").text
        Some(RssNewsEnclosure(url, mime))
      case _ => None
    }
    val pubDate = formatter.parseDateTime((node \\ "pubDate").text)
    new RssNews(title = (node \\ "title").text,
      link = (node \\ "link").text,
      description = (node \\ "description").text,
      pubDate = pubDate,
      enclosure = enclosure,
      parent = id,
      _id = new ObjectId(pubDate.toDate)
    )
  }


  private def checkRssUrlExitestence(url: String): Future[Option[RssUrl]] = {
    urlRepo.findByUrl(url) map {
      case Some(url: RssUrl) => Some(url)
      case _ => None
    }
  }

}

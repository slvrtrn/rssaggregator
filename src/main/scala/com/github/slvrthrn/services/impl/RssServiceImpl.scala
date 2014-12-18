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
    checkRssUrlExistence(urlStr) map {
      case true => None
      case false =>
        val rssUrl = RssUrl(urlStr)
        userRepo.save(user.copy(feed = user.feed + rssUrl._id))
        urlRepo.save(rssUrl)
        Some(rssUrl)
    }
  }

  def loadNews(user: User): Future[Seq[RssNews]] = {
    val urls = urlRepo.find("_id" $in user.feed)
    val now = new DateTime
    for {
      news <- urls flatMap (urlSeq => {
        val result = urlSeq map (
          rssUrl => {
            if (Seconds.secondsBetween(rssUrl.lastUpdate, now).getSeconds > 60)
              Future { (XML.load(rssUrl.url) \\ "item").map(buildNews(_, rssUrl._id)).toSeq }
            else
              Future value Seq()
          })
        Future.collect(result).map(_.flatten)
      })
      _ <- newsRepo.saveT(news)
    } yield news
  }

  private def buildNews(node: Node, id: ObjectId): RssNews = new RssNews(
    title = (node \\ "title").text,
    link = (node \\ "link").text,
    description = (node \\ "description").text,
    parent = id
  )

  private def checkRssUrlExistence(url: String): Future[Boolean] = {
    urlRepo.findOne("url" $eq url) map {
      case Some(url: RssUrl) => true
      case _ => false
    }
  }

}

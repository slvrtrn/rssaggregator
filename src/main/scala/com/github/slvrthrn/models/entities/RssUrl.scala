package com.github.slvrthrn.models.entities

import org.bson.types.ObjectId
import org.joda.time.{DateTimeZone, DateTime}

/**
 * Created by slvr on 12/17/14.
 */
case class RssUrl(

                   url: String,
                   lastUpdate: DateTime = DateTime.now.minusSeconds(61),
                   image: Option[String] = None,
                   _id: ObjectId = new ObjectId

                   ) extends MongoEntity

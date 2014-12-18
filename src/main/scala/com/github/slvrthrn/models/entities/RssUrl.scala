package com.github.slvrthrn.models.entities

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
 * Created by slvr on 12/17/14.
 */
case class RssUrl(

                   url: String,
                   lastUpdate: DateTime = (new DateTime).minusSeconds(61),
                   _id: ObjectId = new ObjectId

                   ) extends MongoEntity

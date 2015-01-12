package com.github.slvrthrn.models.entities

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
 * Created by slvr on 12/17/14.
 */
case class RssNews(

                    title: String,
                    link: String,
                    description: String,
                    pubDate: DateTime,
                    parent: ObjectId,
                    _id: ObjectId = new ObjectId

                    ) extends MongoEntity

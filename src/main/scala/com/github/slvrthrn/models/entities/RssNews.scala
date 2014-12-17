package com.github.slvrthrn.models.entities

import org.bson.types.ObjectId

/**
 * Created by slvr on 12/17/14.
 */
case class RssNews(title: String, link: String, description: String, _id: ObjectId = new ObjectId) extends MongoEntity

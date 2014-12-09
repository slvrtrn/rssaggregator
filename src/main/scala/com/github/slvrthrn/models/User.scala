package com.github.slvrthrn.models

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
 * Created by slvr on 12/7/14.
 */
case class User(login: String, password: String, _id: ObjectId = new ObjectId()) extends MongoEntity

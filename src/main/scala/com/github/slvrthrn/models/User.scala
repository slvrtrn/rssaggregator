package com.github.slvrthrn.models

import org.bson.types.ObjectId
import org.joda.time.DateTime

/**
 * Created by slvr on 12/7/14.
 */
case class User(_id: ObjectId, login: String, email: String, password: String) extends MongoEntity

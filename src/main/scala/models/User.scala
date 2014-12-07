package ru.slvr.models

import org.bson.types.ObjectId

/**
 * Created by slvr on 12/7/14.
 */
case class User(_id: ObjectId, login: String, email: String, password: String, regtime: Long) extends MongoEntity

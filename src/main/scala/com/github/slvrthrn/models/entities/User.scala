package com.github.slvrthrn.models.entities

import org.bson.types.ObjectId

/**
 * Created by slvr on 12/7/14.
 */
case class User(

                 login: String,
                 password: String,
                 feed: Set[String] = Set[String](),
                 _id: ObjectId = new ObjectId

                 ) extends MongoEntity

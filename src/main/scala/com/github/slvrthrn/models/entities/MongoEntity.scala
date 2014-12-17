package com.github.slvrthrn.models.entities

import com.novus.salat.annotations._
import org.bson.types.ObjectId

/**
 * Created by slvr on 12/5/14.
 */
trait MongoEntity {
  @Key("_id") val _id: ObjectId
}

package com.github.slvrthrn.models

import org.bson.types.ObjectId
import com.novus.salat.annotations._

/**
 * Created by slvr on 12/5/14.
 */
trait MongoEntity {
  @Key("_id") val _id: ObjectId
}

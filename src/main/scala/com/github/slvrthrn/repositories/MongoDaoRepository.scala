package com.github.slvrthrn.repositories

import com.github.slvrthrn.models.entities.MongoEntity
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoDB, MongoCollection}
import com.novus.salat.dao.SalatDAO
import com.twitter.util.Future
import org.bson.types.ObjectId
import com.github.slvrthrn.utils.InjectHelper
import scala.concurrent.ExecutionContext
import com.mongodb.casbah.Imports._

trait MongoDaoRepository[O <: MongoEntity] { self: InjectHelper =>

  protected implicit val executionContext = inject[ExecutionContext]

  protected val db: MongoDB = inject[MongoDB]

  protected val collection: MongoCollection

  protected val dao: SalatDAO[O, ObjectId]

  def save(obj: O): Future[O] = Future {
    dao.save(obj)
    obj
  }

  def saveTraversable(t: Traversable[O]): Future[Traversable[O]] = Future {
    dao.insert(t)
    t
  }

  def clearCollection: Future[WriteResult] = Future {
    dao.remove(MongoDBObject())
  }

  def find(filter: MongoDBObject = MongoDBObject(), limit: Int = 0, sort: MongoDBObject = MongoDBObject("_id" -> -1))
  : Future[Seq[O]] = Future {
    dao.find(filter)
      .sort(sort)
      .limit(limit)
      .toList
  }

  def findById(id: ObjectId): Future[Option[O]] = Future {
    val res = dao.findOneById(id)
    res
  }

  def findOne(filter: MongoDBObject): Future[Option[O]] = Future {
    dao.findOne(filter)
  }

  def remove(entity: O): Future[Boolean] = Future {
    dao.remove(entity).getN > 0
  }

  def removeBy(filter: MongoDBObject): Future[Boolean] = Future {
    dao.remove(filter).getN > 0
  }

  def removeById(id: ObjectId): Future[Boolean] = Future {
    dao.removeById(id).getN > 0
  }

  def findAllWithRange(filter: MongoDBObject = MongoDBObject(), startFrom: ObjectId, limit: Int): Future[Seq[O]] = Future {
    dao
      .find(filter ++ ("_id" $lt startFrom))
      .limit(limit)
      .sort(MongoDBObject("_id" -> -1))
      .toList
  }

  def count(filter: MongoDBObject = MongoDBObject()): Future[Long] = Future {
    dao.count(filter)
  }

}

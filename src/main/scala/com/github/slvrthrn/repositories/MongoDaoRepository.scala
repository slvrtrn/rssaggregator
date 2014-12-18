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

/**
 * DAO репозиторий. Стильный, модный, молодежный, чуть-чуть буддисткий.
 *
 * Если каких-то базовых операций не хватает - допиши, будь сильным и смелым, ловким, умелым
 *
 * @tparam O Тип хранимых данных
 */
trait MongoDaoRepository[O <: MongoEntity] { self: InjectHelper =>

  protected implicit val executionContext = inject[ExecutionContext]

  protected val db: MongoDB = inject[MongoDB]

  protected val collection: MongoCollection

  protected val dao: SalatDAO[O, ObjectId]

  /**
   * Апдейт или сохранение новой записи
   * @param obj
   * @return
   */
  def save(obj: O): Future[O] = Future {
    dao.save(obj)
    obj
  }

  /**
   *
   * @param t
   * @return
   */
  def saveT(t: Traversable[O]): Future[Traversable[O]] = Future {
    dao.insert(t)
    t
  }

  /**
   *
   * @return
   */
  def clearCollection: Future[WriteResult] = Future {
    dao.remove(MongoDBObject())
  }

  /**
   *
   * @param filter
   * @return
   */
  def find(filter: MongoDBObject = MongoDBObject()): Future[Seq[O]] = Future {
    val result = dao.find(filter)
      .sort(MongoDBObject("_id" -> -1))
      .toList
    result
  }

  /**
   * Поиск по ObjectID
   * @param id
   * @return
   */
  def findById(id: ObjectId): Future[Option[O]] = Future {
    dao.findOneById(id)
  }

  /**
   * Поиск по фильтру
   * @param filter
   * @return
   */
  def findOne(filter: MongoDBObject): Future[Option[O]] = Future {
    dao.findOne(filter)
  }

  /**
   * Удаление записи
   * @param entity
   * @return
   */
  def remove(entity: O): Future[Boolean] = Future {
    dao.remove(entity).getN > 0
  }

  /**
   * Удаление по фильтру
   * @param filter
   * @return
   */
  def removeBy(filter: MongoDBObject): Future[Boolean] = Future {
    dao.remove(filter).getN > 0
  }

  /**
   * Удаление по ID
   * @param id
   * @return
   */
  def removeById(id: ObjectId): Future[Boolean] = Future {
    dao.removeById(id).getN > 0
  }

  /**
   * Поиск всех подпадающих под фильтр записей с использованием range based paging
   * @param filter
   * @param startFrom
   * @param limit
   * @return
   */
  def findAllWithRange(filter: MongoDBObject = MongoDBObject(), startFrom: ObjectId, limit: Int): Future[Seq[O]] = Future {
    dao
      .find(filter ++ ("_id" $lt startFrom))
      .limit(limit)
      .sort(MongoDBObject("_id" -> -1))
      .toList
  }

  /**
   * Кол-во записей, подпадающих под фильтр
   * @param filter
   * @return
   */
  def count(filter: MongoDBObject = MongoDBObject()): Future[Long] = Future {
    dao.count(filter)
  }

}

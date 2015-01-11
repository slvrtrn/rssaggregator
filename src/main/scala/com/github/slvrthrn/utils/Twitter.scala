package com.github.slvrthrn.utils

import com.twitter.util.{Future => TwFuture, Promise => TwPromise}
import scala.concurrent.{Future => ScFuture, promise => scPromise, ExecutionContext}

object Twitter {
  implicit class TwFutureToScala[T](val tf: TwFuture[T]) extends AnyVal {
    def asScala: ScFuture[T] = {
      val prom = scPromise[T]()
      tf.onSuccess { prom success _ }
      tf.onFailure { prom failure _ }
      prom.future
    }
  }

  implicit class ScFutureToTwitter[T](val sf: ScFuture[T]) extends AnyVal {
    def asTwitter(implicit ec: ExecutionContext): TwFuture[T] = {
      val prom = TwPromise[T]()
      // type inference issue
      sf onSuccess PartialFunction(prom.setValue)
      sf onFailure { case t: Throwable => prom setException t }
      prom
    }
  }
}

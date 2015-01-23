package com.github.slvrthrn.config

import scaldi.MutableInjectorAggregation

/**
 * Created by slvr on 12/6/14.
 */
object BindingsProvider {
  def getBindings: MutableInjectorAggregation = new CommonConfiguration :: new Repositories :: new Services :: new Cache
  def getTestBindings: MutableInjectorAggregation = new TestConfiguration :: new Repositories :: new Services :: new Cache
}

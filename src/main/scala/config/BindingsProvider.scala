package ru.slvr.config

/**
 * Created by slvr on 12/6/14.
 */
object BindingsProvider {
  def getBindings = new CommonConfiguration :: new Repositories :: new Services
}

package com.github.slvrthrn

import com.twitter.finatra.FinatraServer
import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.{RssController, NewsController, AuthController, IndexController}
import com.github.slvrthrn.filters.IndexFilter

/**
 * Created by slvr on 12/9/14.
 */
object App extends FinatraServer {

  System.setProperty("com.twitter.finatra.config.docRoot", ".")
  System.setProperty("com.twitter.finatra.config.assetPath", "/public")

  private implicit val inj = BindingsProvider.getBindings

  addFilter(new IndexFilter)
  register(new AuthController)
  register(new IndexController)
  register(new NewsController)
  register(new RssController)
}

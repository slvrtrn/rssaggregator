package com.github.slvrthrn

import com.twitter.finatra.FinatraServer
import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.{Auth, Feed, Index}
import com.github.slvrthrn.filters.AuthFilter

/**
 * Created by slvr on 12/9/14.
 */
object App extends FinatraServer {
  private implicit val inj = BindingsProvider.getBindings
  addFilter(new AuthFilter)
  register(new Index)
  register(new Auth)
  register(new Feed)
}

package com.github.slvrthrn

import com.twitter.finatra.FinatraServer
import com.github.slvrthrn.config.BindingsProvider
import com.github.slvrthrn.controllers.{Auth, Index}
import com.github.slvrthrn.filters.IndexFilter
import org.joda.time.DateTimeZone

/**
 * Created by slvr on 12/9/14.
 */
object App extends FinatraServer {

  DateTimeZone.setDefault(DateTimeZone.forID("Europe/Moscow"))

  private implicit val inj = BindingsProvider.getBindings

  addFilter(new IndexFilter)

  register(new Index)
  register(new Auth)

}

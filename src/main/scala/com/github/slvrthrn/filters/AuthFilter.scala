package com.github.slvrthrn.filters

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finatra.Logging
import com.twitter.util.Future
import com.twitter.finagle.http.{Request => FinagleRequest}
import com.twitter.finagle.http.{Response => FinagleResponse}
import com.twitter.app.App

class AuthFilter
  extends SimpleFilter[FinagleRequest, FinagleResponse] with App with Logging  {

  def apply(
             request: FinagleRequest,
             service: Service[FinagleRequest, FinagleResponse]
             ) = {
    val start = System.currentTimeMillis()
    service(request) map { response =>
      val end = System.currentTimeMillis()
      val duration = end - start
      log.info("%s".format(request.getParams()))
      response
    }
  }
}
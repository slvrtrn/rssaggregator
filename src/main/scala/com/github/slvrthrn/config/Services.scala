package com.github.slvrthrn.config

import com.github.slvrthrn.services._
import scaldi.Module
import com.github.slvrthrn.services.impl.{RssServiceImpl, SessionServiceImpl, UserServiceImpl}

/**
 * Created by slvr on 12/6/14.
 */
class Services extends Module {
  bind [UserService] to new UserServiceImpl
  bind [SessionService] to new SessionServiceImpl
  bind [RssService] to new RssServiceImpl
}

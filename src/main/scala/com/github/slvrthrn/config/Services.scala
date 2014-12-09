package com.github.slvrthrn.config

import com.github.slvrthrn.services._
import scaldi.Module
import com.github.slvrthrn.services.impl.{UserServiceImpl, MessageServiceImpl}

/**
 * Created by slvr on 12/6/14.
 */
class Services extends Module {
  bind [UserService] to new UserServiceImpl
  bind [MessageService] to new MessageServiceImpl
}

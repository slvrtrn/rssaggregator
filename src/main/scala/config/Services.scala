package ru.slvr.config

import ru.slvr.services._
import scaldi.Module
import ru.slvr.services.impl.{UserServiceImpl, MessageServiceImpl}

/**
 * Created by slvr on 12/6/14.
 */
class Services extends Module {
  bind [UserService] to new UserServiceImpl
  bind [MessageService] to new MessageServiceImpl
}

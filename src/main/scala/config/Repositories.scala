package ru.slvr.config

import ru.slvr.repositories.impl.{UserRepoImpl, MessageRepoImpl}
import ru.slvr.repositories._
import scaldi.Module

/**
 * Created by slvr on 12/6/14.
 */
class Repositories extends Module {
  bind [MessageRepo] to new MessageRepoImpl
  bind [UserRepo] to new UserRepoImpl
}

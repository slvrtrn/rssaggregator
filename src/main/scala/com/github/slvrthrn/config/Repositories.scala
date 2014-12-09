package com.github.slvrthrn.config

import com.github.slvrthrn.repositories.impl.{UserRepoImpl, MessageRepoImpl}
import com.github.slvrthrn.repositories._
import scaldi.Module

/**
 * Created by slvr on 12/6/14.
 */
class Repositories extends Module {
  bind [MessageRepo] to new MessageRepoImpl
  bind [UserRepo] to new UserRepoImpl
}

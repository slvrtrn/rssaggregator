package com.github.slvrthrn.config

import com.github.slvrthrn.repositories.impl.UserRepoImpl
import com.github.slvrthrn.repositories._
import scaldi.Module

/**
 * Created by slvr on 12/6/14.
 */
class Repositories extends Module {
  bind [UserRepo] to new UserRepoImpl
}

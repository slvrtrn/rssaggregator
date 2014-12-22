package com.github.slvrthrn.config

import com.github.slvrthrn.repositories.cache.{RssUrlCache, RssNewsCache, UserCache}
import com.github.slvrthrn.repositories.impl.{RssNewsRepoImpl, RssUrlRepoImpl, UserRepoImpl}
import com.github.slvrthrn.repositories._
import scaldi.Module

/**
 * Created by slvr on 12/6/14.
 */
class Repositories extends Module {
  bind [UserRepo] to new UserCache
  //bind [UserRepo] to new UserRepoImpl
  bind [RssUrlRepo] to new RssUrlCache
  //bind [RssUrlRepo] to new RssUrlRepoImpl
  bind [RssNewsRepo] to new RssNewsCache
  //bind [RssNewsRepo] to new RssNewsRepoImpl
}

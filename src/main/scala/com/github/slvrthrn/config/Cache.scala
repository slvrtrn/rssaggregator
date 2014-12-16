package com.github.slvrthrn.config

import com.github.slvrthrn.repositories.cache.{UserCache, SessionCache}
import scaldi.Module

/**
 * Created by slvr on 12/11/14.
 */
class Cache extends Module {
  bind [SessionCache] to new SessionCache
}

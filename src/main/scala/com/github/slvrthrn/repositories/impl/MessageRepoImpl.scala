package com.github.slvrthrn.repositories.impl

import com.github.slvrthrn.repositories.MessageRepo
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class MessageRepoImpl(implicit val inj: Injector) extends MessageRepo with Injectable {

}

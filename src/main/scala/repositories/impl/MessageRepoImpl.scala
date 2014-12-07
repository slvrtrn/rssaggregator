package ru.slvr.repositories.impl

import ru.slvr.repositories.MessageRepo
import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/6/14.
 */
class MessageRepoImpl(implicit val inj: Injector) extends MessageRepo with Injectable {

}

package ru.slvr.repositories.impl

import ru.slvr.repositories.UserRepo
import ru.slvr.utils.InjectHelper
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class UserRepoImpl(implicit val inj: Injector) extends UserRepo with InjectHelper{

}

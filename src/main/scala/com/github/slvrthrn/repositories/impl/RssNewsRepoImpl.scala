package com.github.slvrthrn.repositories.impl

import com.github.slvrthrn.repositories.RssNewsRepo
import com.github.slvrthrn.utils.InjectHelper
import scaldi.Injector

/**
 * Created by slvr on 12/17/14.
 */
class RssNewsRepoImpl(implicit val inj: Injector) extends RssNewsRepo with InjectHelper {

}

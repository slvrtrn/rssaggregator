package com.github.slvrthrn.utils

import scaldi.{Injectable, Injector}

/**
 * Created by slvr on 12/7/14.
 */
trait InjectHelper extends Injectable{
  implicit val inj: Injector
}

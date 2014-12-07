package ru.slvr.controllers

import ru.slvr.models.User
import ru.slvr.services.UserService
import scaldi.Injector

/**
 * Created by slvr on 12/6/14.
 */
class Auth(implicit val inj: Injector) extends Controller {
  val userService = inject[UserService]

  get("/auth") { request =>
    userService.getAllUsers.flatMap(renderJsonArray[User]).rescue({
      case e => render.plain(e.getMessage()).toFuture
    })
  }
}
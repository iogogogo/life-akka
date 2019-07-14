package com.github.akka.routes

import java.util

import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix, _}
import akka.http.scaladsl.server.Route
import com.github.akka.protocol.BaseProtocol.UserVO
import com.github.akka.util.IdHelper
import com.github.akka.util.JsonParse._
import com.github.akka.wapper.ResultWrapper

/**
  * Created by tao.zeng on 2019-07-14.
  */
class MainRoute extends ResultWrapper {

  val route: Route = pathPrefix("api") {
    path("main") {
      get {
        complete(successful(data = list()))
      }
    }
  }

  def list(): util.List[UserVO] = {
    val list = new util.ArrayList[UserVO]()
    list.add(UserVO(IdHelper.id(), s"哈哈哈-${IdHelper.id()}", IdHelper.uuid()))
    list.add(UserVO(IdHelper.id(), s"嘿嘿嘿-${IdHelper.id()}", IdHelper.uuid()))
    list.add(UserVO(IdHelper.id(), s"嘻嘻嘻-${IdHelper.id()}", IdHelper.uuid()))
    list
  }
}

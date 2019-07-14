package com.github.akka.routes

import java.time.LocalDateTime

import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix, _}
import akka.http.scaladsl.server.Route
import com.github.akka.context.BaseDirectives
import com.github.akka.context.ImperativeRequestContext._
import com.github.akka.service.IndexService
import com.github.akka.util.JsonParse._
import com.github.akka.wapper.ResultWrapper

/**
  * Created by tao.zeng on 2019-07-14.
  */
class IndexRoute extends BaseDirectives with ResultWrapper {

  val indexService = new IndexService()

  val route: Route = pathPrefix("api") {
    apiAuthentication { _ =>
      path("xxx") {
        post {
          entity(as[String]) {
            request =>
              complete(successful(message = "POST", data = request))
          }
        } ~
          get {
            imperativelyComplete {
              ctx =>
                indexService.index(ctx)
            }
          }
      } ~
        path("index") {
          get {
            complete(successful(message = "GET", data = LocalDateTime.now()))
          } ~
            post {
              complete(successful(message = "POST", data = LocalDateTime.now()))
            }
        } ~
        path("index" / LongNumber) {
          id =>
            get {
              complete(successful(message = "GET", data = s"id=>$id"))
            } ~ put {
              entity(as[String]) {
                request =>
                  complete(successful(message = "PUT", data = s"id=>$id request=>$request"))
              }
            }
        }
    }
  }
}

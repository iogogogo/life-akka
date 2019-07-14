package com.github.akka.context

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.headers.CacheDirectives.{`max-age`, `no-cache`}
import akka.http.scaladsl.model.headers.{RawHeader, `Access-Control-Allow-Methods`, `Cache-Control`}
import akka.http.scaladsl.server.Directives.{complete, mapResponseHeaders, optionalHeaderValueByName, options, parameterMap, provide, _}
import akka.http.scaladsl.server.{Directive0, Directive1, Route}
import com.github.akka.constant.BaseConsts

/**
  * Created by tao.zeng on 2019-07-14.
  */
trait CrossSupport {

  private def addAccessControlHeaders(): Directive0 = {
    mapResponseHeaders {
      headers =>
        `Cache-Control`(`no-cache`, `max-age`(0)) +:
          RawHeader("x-version", BaseConsts.VERSION) +:
          headers
    }
  }

  private def preFlightRequestHandler: Route = options {
    complete(HttpResponse(OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def crossHandler(route: Route): Route = addAccessControlHeaders() {
    preFlightRequestHandler ~ route
  }

  def extractAuth(headParam: String): Directive1[Option[String]] = {
    optionalHeaderValueByName(headParam).flatMap {
      case Some(auth) =>
        provide(Some(auth))
      case None =>
        parameterMap.flatMap { key =>
          key.get(BaseConsts.AUTH) match {
            case Some(auth) =>
              provide(Some(auth))
            case None =>
              provide(None)
          }
        }
    }
  }
}

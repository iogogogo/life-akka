package com.github.akka.handler

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.akka.constant.BaseConsts
import org.slf4j.LoggerFactory
import com.github.akka.context.CrossSupport
import com.github.akka.routes.{IndexRoute, MainRoute}

/**
  * Created by tao.zeng on 2019-07-14.
  */
object RouteHandler extends CrossSupport {
  private val logger = LoggerFactory.getLogger(getClass)

  def handler(): Route = {
    extractAuth(BaseConsts.AUTH) {
      auth => {
        logger.info(s"auth:$auth")
        new IndexRoute().route ~ new MainRoute().route
      }
    }
  }
}

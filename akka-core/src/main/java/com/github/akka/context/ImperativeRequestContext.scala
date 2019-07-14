package com.github.akka.context


import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}

import scala.concurrent.{ExecutionContextExecutor, Promise}

/**
  * Created by tao.zeng on 2019-07-14.
  */
final class ImperativeRequestContext(ctx: RequestContext, promise: Promise[RouteResult]) {

  private implicit val context: ExecutionContextExecutor = ctx.executionContext

  private implicit val request: HttpRequest = ctx.request

  def complete(obj: ToResponseMarshallable): Unit = ctx.complete(obj).onComplete(promise.complete)

  def failure(error: Throwable): Unit = ctx.fail(error).onComplete(promise.complete)

}

object ImperativeRequestContext {
  def imperativelyComplete(inner: ImperativeRequestContext => Unit): Route = {
    ctx: RequestContext =>
      val p = Promise[RouteResult]()
      inner(new ImperativeRequestContext(ctx, p))
      p.future
  }
}
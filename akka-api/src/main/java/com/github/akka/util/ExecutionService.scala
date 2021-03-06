package com.github.akka.util

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

/**
  * Created by tao.zeng on 2019-07-14.
  */
object ExecutionService {

  private[this] val THREAD_POLL: ExecutorService = Executors.newFixedThreadPool(10)

  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(THREAD_POLL)

  implicit lazy val system: ActorSystem = ActorSystem("akka-api")

  implicit lazy val mat: ActorMaterializer = ActorMaterializer()

}

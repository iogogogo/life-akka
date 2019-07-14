package com.github.akka

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.github.akka.conf.ServerConf
import com.github.akka.handler.RouteHandler
import com.github.akka.util.JsonParse
import com.github.akka.wapper.ResultWrapper
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

/**
  * Created by tao.zeng on 2019-07-14.
  */
object AkkaApplication extends ResultWrapper {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  import com.github.akka.util.ExecutionService._

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(RouteHandler.handler(), ServerConf.ip(), ServerConf.port()).onComplete {
      case Success(result) =>
        logger.info(s"start ${JsonParse.toJson(result)} successful")
      case Failure(ex) => logger.error(s"starter failure！$ex")
    }
  }

  implicit def globalExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex: ArithmeticException =>
        extractUri { uri =>
          logger.error(s"uri:[$uri] ,error msg:${ex.getMessage}", ex)
          complete(HttpResponse(InternalServerError, entity = ex.getMessage))
        }
      case ex: NullPointerException =>
        complete(HttpResponse(StatusCodes.NotFound, entity = JsonParse.toJson(failure(message = ex.getMessage, data = null))))
      case ex: Exception =>
        logger.error(s"error msg:${ex.getMessage}", ex)
        complete(HttpResponse(InternalServerError, entity = JsonParse.toJson(failure(message = ex.getMessage, data = null))))
    }
}

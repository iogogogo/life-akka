package com.github.akka.protocol

/**
  * Created by tao.zeng on 2019-07-14.
  */
object Protocol {

  case class ResponseWrapper(code: Int, message: String, data: Any)

  case class PageWrapper(current: Int, size: Int, total: Long, data: Any)
}

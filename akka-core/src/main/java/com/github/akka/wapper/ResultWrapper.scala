package com.github.akka.wapper

import com.github.akka.protocol.Protocol.ResponseWrapper

/**
  * Created by tao.zeng on 2019-07-14.
  */
trait ResultWrapper {

  def result(code: Int, message: String, data: Any) = ResponseWrapper(code, message, data)

  def successful(code: Int = 200, message: String = "ok!", data: Any): ResponseWrapper = result(code, message, data)

  def failure(code: Int = 500, message: String = "failure.", data: Any): ResponseWrapper = result(code, message, data)
}

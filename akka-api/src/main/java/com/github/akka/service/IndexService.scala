package com.github.akka.service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.github.akka.context.ImperativeRequestContext
import com.github.akka.util.JsonParse._
import com.github.akka.wapper.ResultWrapper

/**
  * Created by tao.zeng on 2019-07-14.
  */
class IndexService extends ResultWrapper {

  def index(ctx: ImperativeRequestContext): Unit = {
    val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    ctx.complete(successful(message = "小花脸", data = now))
  }
}

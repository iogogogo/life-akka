package com.github.akka.conf

import java.util.concurrent.ConcurrentHashMap


/**
  * Created by tao.zeng on 2019-07-14.
  */
class SnowflakeConf extends ClientConf[SnowflakeConf] {

  override val configs: ConcurrentHashMap[String, Any] = getConfigs("snowflake")

  def workerId(): Int = get[Int]("workerId").getOrElse(0)
}

object SnowflakeConf extends SnowflakeConf
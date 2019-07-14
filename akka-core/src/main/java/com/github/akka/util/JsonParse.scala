package com.github.akka.util

import java.lang.reflect.{ParameterizedType, Type}
import java.math.BigInteger

import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import mesosphere.jackson.CaseClassModule

import scala.reflect.ClassTag

/**
  * Created by tao.zeng on 2018/9/11.
  */
object JsonParse extends JacksonSupport {

  def toJson(value: Any): String = {
    defaultObjectMapper.writeValueAsString(value)
  }

  //  def fromJson[T](json: String)(implicit m: Manifest[T]): T = {
  //    defaultObjectMapper.readValue[T](json)
  //  }

  def fromJson[T: Manifest](json: String): T = {
    defaultObjectMapper.readValue[T](json, typeReference[T])
  }

  def typeReference[T: Manifest]: TypeReference[T] = new TypeReference[T] {
    override def getType: Type = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type =
    if (m.typeArguments.isEmpty) m.runtimeClass
    else new ParameterizedType {
      override def getRawType: Class[_] = m.runtimeClass

      override def getActualTypeArguments: Array[Type] = m.typeArguments.map(typeFromManifest).toArray

      override def getOwnerType: Null = null
    }
}

trait JacksonSupport {

  val defaultObjectMapper = new ObjectMapper() with ScalaObjectMapper
  val module = new DefaultScalaModule with CaseClassModule

  // Java8 日期序列化反序列化处理
  defaultObjectMapper.registerModule(new JavaTimeModule());
  defaultObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  // defaultObjectMapper.registerModule(DefaultScalaModule)
  defaultObjectMapper.registerModule(module)

  defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  defaultObjectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
  defaultObjectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
  defaultObjectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)

  // null 不序列化
  defaultObjectMapper.setSerializationInclusion(Include.NON_NULL)

  val simpleModule = new SimpleModule();

  /**
    * 将Long,BigInteger序列化的时候,转化为String
    */
  simpleModule.addSerializer(classOf[java.lang.Long], ToStringSerializer.instance);
  simpleModule.addSerializer(classOf[BigInteger], ToStringSerializer.instance);

  private val jsonStringUnmarshaller: FromEntityUnmarshaller[String] =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(`application/json`)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset) => data.decodeString(charset.nioCharset.name)
      }

  /**
    * HTTP entity => `A`
    */
  implicit def jacksonUnmarshaller[A](implicit ct: ClassTag[A],
                                      objectMapper: ScalaObjectMapper = defaultObjectMapper,
                                      m: Manifest[A]): FromEntityUnmarshaller[A] = {
    jsonStringUnmarshaller.map(
      //data => objectMapper.readValue(data, ct.runtimeClass).asInstanceOf[A]
      data => objectMapper.readValue(data)
    )
  }

  /**
    * `A` => HTTP entity
    */
  implicit def jacksonToEntityMarshaller[Object](implicit objectMapper: ObjectMapper = defaultObjectMapper): ToEntityMarshaller[Object] = {
    Jackson.marshaller[Object](objectMapper)
  }
}
package com.raagnair.nani.scribes

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.raagnair.nani.interfaces.Scribe
import com.raagnair.nani.util.TypeUtil.fetchType

object JsonScribe extends Scribe {
  val MAPPER: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  override def deserialize[T: Manifest](str: String): T = MAPPER.readValue(str, typeReference[T])
  override def serialize(thing: Any): String = MAPPER.writeValueAsString(thing)

  private def typeReference[T: Manifest]: TypeReference[T] = new TypeReference[T] {
    override def getType = fetchType(manifest[T])
  }
}

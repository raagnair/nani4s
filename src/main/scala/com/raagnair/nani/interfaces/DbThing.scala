package com.raagnair.nani.interfaces

import com.raagnair.nani.util.NaniDbUtil
import scala.reflect.{classTag, ClassTag}

object DbThing {
  val KEY_FIELD_SEPARATOR: String = "_"

  def deserialize[T <: DbThing: Manifest](str: String)(implicit s: Scribe): T = s.deserialize[T](str)
  def serialize(thing: DbThing)(implicit s: Scribe): String = s.serialize(thing)
  def key(thing: DbThing): String = thing.keyFields.mkString(KEY_FIELD_SEPARATOR)
  def table[T <: DbThing: ClassTag]: String = NaniDbUtil.camelToSnake(classTag[T].runtimeClass.getSimpleName)
}

trait DbThing {
  def keyFields: Seq[Any]
}

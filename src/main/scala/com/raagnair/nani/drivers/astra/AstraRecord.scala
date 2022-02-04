package com.raagnair.nani.drivers.astra

import com.raagnair.nani.interfaces.{DbThing, RecordWrapperLike}
import com.raagnair.nani.scribes.JsonScribe

import scala.reflect.ClassTag

case class AstraRecord[T <: DbThing: ClassTag: Manifest](id: String, data: T, modified: Long)
    extends RecordWrapperLike[T] {
  def toDbFormat: AstraRecordToDb[T] = AstraRecordToDb[T](id, JsonScribe.serialize(data), modified)
}
case class AstraRecordToDb[T <: DbThing: ClassTag: Manifest](id: String, data: String, modified: Long) {
  def toRecord[T <: DbThing: ClassTag: Manifest]: AstraRecord[T] =
    AstraRecord(id, JsonScribe.deserialize[T](data), modified)
}
case class AstraRecordFromDb[T <: DbThing: ClassTag: Manifest](id: String, data: String, modified: AstraTimestamp) {
  def toRecord[T <: DbThing: ClassTag: Manifest]: AstraRecord[T] =
    AstraRecord(id, JsonScribe.deserialize[T](data), modified.toTime)
}
case class AstraTimestamp(nano: Long, epochSecond: Long) {
  def toTime: Long = (epochSecond * 1_000) + (nano / 1_000_000)
}

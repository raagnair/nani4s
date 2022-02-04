package com.raagnair.nani.drivers

import com.raagnair.nani.interfaces.DbThing

import java.util.function.Consumer
import scala.reflect.ClassTag

object WriteResult {
  def apply[T <: DbThing: ClassTag](error: Exception): WriteResult = WriteResult(error = Some(error))
  def apply[T <: DbThing: ClassTag](error: String): WriteResult = WriteResult(error = Some(new Exception(error)))
}

case class WriteResult(error: Option[Exception] = None, metric: NaniMetric = NaniMetric()) {
  def handle(e: Consumer[Exception]): WriteResult = {
    if (error.isDefined) e.accept(error.get)
    this
  }
}

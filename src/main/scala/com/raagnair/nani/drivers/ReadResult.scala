package com.raagnair.nani.drivers

import com.raagnair.nani.interfaces.{DbThing, RecordWrapperLike}

import java.util.function.Consumer
import scala.reflect.ClassTag

object ReadResult {
  def apply[T <: DbThing: ClassTag](inputData: Iterable[RecordWrapperLike[T]]): ReadResult[T] =
    ReadResult(Some(inputData.toVector))
  def apply[T <: DbThing: ClassTag](error: Exception): ReadResult[T] = ReadResult[T](None, error = Some(error))
  def apply[T <: DbThing: ClassTag](error: String): ReadResult[T] = apply(new Exception(error))
}

/*
  Encapsulates the query result, metrics, and errors.
  Implements Iterable so that .map and similar functions can be called.
  Provides the [handle] function to allow for convenient error handling in method chains.
 */
case class ReadResult[T <: DbThing](
    data: Option[Vector[RecordWrapperLike[T]]],
    metrics: NaniMetric = NaniMetric(),
    error: Option[Exception] = None)
    extends Iterable[T] {
  override def iterator: Iterator[T] = data.getOrElse(Vector()).map(_.data).iterator
  def withMetadata: Iterator[RecordWrapperLike[T]] = data.getOrElse(Vector()).iterator

  def handle(e: Consumer[Exception]): ReadResult[T] = {
    if (error.isDefined) e.accept(error.get)
    this
  }

  override def toString: String = {
    if (error.isDefined) error.get.toString
    else if (data.isDefined) data.get.toString
    else "no data, no error"
  }
}

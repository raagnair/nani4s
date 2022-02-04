package com.raagnair.nani.interfaces

import com.raagnair.nani.drivers.{ReadResult, WriteResult}

import scala.reflect.ClassTag

trait DbDriver {
  def insert[T <: DbThing: ClassTag: Manifest](thing: T): WriteResult
  def make[T <: DbThing: ClassTag: Manifest]: WriteResult

  def read[T <: DbThing: ClassTag: Manifest](key: String): ReadResult[T]
  def readAll[T <: DbThing: ClassTag: Manifest]: ReadResult[T]
}

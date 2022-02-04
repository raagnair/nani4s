package com.raagnair.nani.interfaces

trait Scribe {
  def serialize(thing: Any): String
  def deserialize[T: Manifest](str: String): T
}

package com.raagnair.nani.util

import java.lang.reflect.{ParameterizedType, Type}
import java.util.concurrent.ConcurrentHashMap
import scala.annotation.tailrec
import scala.reflect.{classTag, ClassTag}

/*
  Necessary for clean Json -> Scala object deserialization.
  https://stackoverflow.com/questions/12591457/ | archive: https://archive.is/gQfHi

 */
object TypeUtil {
  private val TYPE_MAP: ConcurrentHashMap[Manifest[_], Type] = new ConcurrentHashMap()
  val x = classTag[String]

  def fetchType(m: Manifest[_]): Type = {
    TYPE_MAP.computeIfAbsent(m, calculateType)
  }

  private[this] def calculateType(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else
      new ParameterizedType {
        def getRawType: Class[_] = m.runtimeClass
        def getActualTypeArguments: Array[Type] = m.typeArguments.map(calculateType).toArray
        def getOwnerType: Type = null
      }
  }

  def camelToSnake(name: String): String = {
    @tailrec
    def go(accDone: List[Char], acc: List[Char]): List[Char] = acc match {
      case Nil                                                        => accDone
      case a :: b :: c :: tail if a.isUpper && b.isUpper && c.isLower => go(accDone ++ List(a, '_', b, c), tail)
      case a :: b :: tail if a.isLower && b.isUpper                   => go(accDone ++ List(a, '_', b), tail)
      case a :: tail                                                  => go(accDone :+ a, tail)
    }
    go(Nil, name.toList).mkString.toLowerCase
  }
}

package com.raagnair.nani.drivers.astra

import com.raagnair.nani.drivers.astra.AstraUrl.{BASE, BASE_TABLES}
import com.raagnair.nani.interfaces.DbThing

import scala.reflect.ClassTag

case class AstraUrl[T <: DbThing: ClassTag](config: AstraConnectionConfig) {
  private lazy val id = config.id
  private lazy val ks = config.keyspace
  private lazy val rg = config.region

  def insert(thing: T) = s"https://$id-$rg$BASE$ks/${DbThing.table[thing.type]}"
  def read(key: String) = s"https://$id-$rg$BASE$ks/${DbThing.table[T]}/$key"
  lazy val readAll = s"https://$id-$rg$BASE$ks/${DbThing.table[T]}/rows"
  lazy val make = s"https://$id-$rg$BASE_TABLES$ks/tables"
}

object AstraUrl {
  val BASE = ".apps.astra.datastax.com/api/rest/v2/keyspaces/"
  val BASE_TABLES = ".apps.astra.datastax.com/api/rest/v2/schemas/keyspaces/"
}

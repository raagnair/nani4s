package com.raagnair.nani.drivers.astra

import com.fasterxml.jackson.databind.JsonNode
import com.raagnair.nani.drivers.http.{NaniHttp, NaniHttpMethod}
import com.raagnair.nani.drivers.{NaniMetric, ReadResult, WriteResult}
import com.raagnair.nani.interfaces.{DbDriver, DbThing, Scribe}
import com.raagnair.nani.scribes.JsonScribe
import scalaj.http._

import scala.reflect.ClassTag

/** The AstraDriver is an implementation of DbDriver that connects to AstraDB's REST API. There is a Scala driver
  * available but it looks ugly and not reusable with other data layers.
  *
  * @param config - defaults to loading from env vars. See [[AstraConnectionConfig]] for the env var names read.
  * @param http - defaults to Http, which is the default entrypoint of the ScalaJ Http library. This parameter is
  *             exposed to fasciliate testing.
  */
case class AstraDriver(
    config: AstraConnectionConfig = AstraConnectionConfig.fromEnvVars,
    http: NaniHttp = NaniHttp(Http))
    extends DbDriver {

  implicit private val scribe: Scribe = JsonScribe

  override def insert[T <: DbThing: ClassTag: Manifest](thing: T): WriteResult = insert(thing, true)

  def insert[T <: DbThing: ClassTag: Manifest](thing: T, makeTable: Boolean): WriteResult = try {
    val startTime = System.currentTimeMillis()
    val record = AstraRecord(DbThing.key(thing), thing, startTime)
    val serialized = scribe.serialize(record.toDbFormat)
    val jsonResponse = http(AstraUrl[T](config).insert(thing),
                            NaniHttpMethod.POST,
                            serialized,
                            Seq(("content-type", "application/json"), ("x-cassandra-token", config.appToken)))
    val metric = NaniMetric(System.currentTimeMillis() - startTime, serialized.length, 0)
    val rootNode = JsonScribe.MAPPER.readTree(jsonResponse)
    val failureNode = rootNode.get("description")
    if (failureNode != null) {
      if (failureNode.toString.contains("Resource not found: table ") && makeTable) {
        make[T]
        insert[T](thing, false)
      } else {
        WriteResult(formatError(rootNode)).copy(metric = metric)
      }
    } else WriteResult(metric = metric)
  } catch {
    case e: Exception => WriteResult(e)
  }

  override def read[T <: DbThing: ClassTag: Manifest](key: String): ReadResult[T] = httpRead(
    AstraUrl[T](config).read(key))

  override def readAll[T <: DbThing: ClassTag: Manifest]: ReadResult[T] = httpRead(AstraUrl[T](config).readAll)

  override def make[T <: DbThing: ClassTag: Manifest]: WriteResult = synchronized {
    val startTime = System.currentTimeMillis()
    val serialized = AstraConstants.getTableCreatorJson(DbThing.table[T])
    val jsonResponse = http(AstraUrl[T](config).make,
                            NaniHttpMethod.POST,
                            serialized,
                            Seq(("content-type", "application/json"), ("x-cassandra-token", config.appToken)))
    val metric = NaniMetric.of(startTime, serialized.length)
    val rootNode = JsonScribe.MAPPER.readTree(jsonResponse)
    if (rootNode.get("description") != null) WriteResult(formatError(rootNode)).copy(metric = metric)
    else WriteResult(metric = metric)
  }

  private def httpRead[T <: DbThing: ClassTag: Manifest](url: String): ReadResult[T] = {
    val startTime = System.currentTimeMillis()
    try {
      val response = parseReadJson(http(url, NaniHttpMethod.GET, "", Seq(("x-cassandra-token", config.appToken))))
      val metric = NaniMetric.of(startTime, 0, response._2)
      response._1 match {
        case Left(rows)   => ReadResult(Some(rows), metrics = metric)
        case Right(error) => ReadResult(error).copy(metrics = metric)
      }
    } catch {
      case e: Exception =>
        ReadResult(e).copy(metrics = NaniMetric.of(startTime))
    }
  }

  private def parseReadJson[T <: DbThing: ClassTag: Manifest](
      json: String): (Either[Vector[AstraRecord[T]], String], Long) = {
    val responseBytes: Long = json.length
    val rootNode: JsonNode = JsonScribe.MAPPER.readTree(json)
    val dataNode: JsonNode = rootNode.get("data")
    if (dataNode == null) (Right(formatError(rootNode)), responseBytes)
    else {
      var rv = Vector[AstraRecord[T]]()
      dataNode.forEach(d => rv = rv :+ JsonScribe.deserialize[AstraRecordFromDb[T]](d.toString).toRecord)
      (Left(rv), responseBytes)
    }
  }

  private def formatError(node: JsonNode): String = s"${node.get("code")} - ${node.get("description")}"
}

/*
CREATE TABLE TestThing ( id text PRIMARY KEY, body text);
insert into testthing (id, body) values ('id_1', 'body this!');
create table test_thing (id text PRIMARY KEY, data text, modified timestamp);
insert into test_thing (id, data, modified) values ('id_1', 'some data', 1643524902);
{"count":2,"data":[{"body":"body that WOW!","id":"id_2"},{"body":"body this!","id":"id_1"}]}
404 - "Resource not found: table 'test_thing_other' not found"

write result: {"description":"Resource not found: table 'testthingother' not found","code":404}
 */

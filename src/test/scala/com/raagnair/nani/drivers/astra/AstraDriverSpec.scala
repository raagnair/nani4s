package com.raagnair.nani.drivers.astra

import com.raagnair.nani.drivers.http.{NaniHttp, NaniHttpMethod}
import com.raagnair.nani.interfaces.DbThing
import org.mockito.ArgumentMatchers
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

/** Test to ensure that AstraDriver is interacting with the Astra REST API properly.
  * For the big ugly escaped strings, use https://www.freeformatter.com/json-escape.html#ad-output to unescape them.
  *
  * REST API docs: https://docs.datastax.com/en/astra/docs/getting-started-with-datastax-astra.html
  */
class AstraDriverSpec extends AsyncFlatSpec with MockitoSugar {
  private val testConfig = AstraConnectionConfig("test_id", "test_region", "test_keyspace", "test_token")
  private def initTest: (AstraDriver, NaniHttp) = {
    val http = mock[NaniHttp]
    val driver = AstraDriver(testConfig, http)
    (driver, http)
  }

  "AstraDriver" should "make: report error" in {
    val (driver, http) = initTest
    val apiError = """{"description":"some error", "code":"some code"}""".stripMargin
    val expectedUrl =
      "https://test_id-test_region.apps.astra.datastax.com/api/rest/v2/schemas/keyspaces/test_keyspace/tables"
    val expectedTableDef =
      "{\"name\":\"test_db_thing\",\"ifNotExists\":true,\"columnDefinitions\": [ " +
        "{\"name\":\"id\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"data\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"modified\",\"typeDefinition\":\"timestamp\",\"static\":false}]," +
        "\"primaryKey\": {\"partitionKey\":[\"id\"]},\"tableOptions\":{\"defaultTimeToLive\":0}}"
    val expectedHttpMethod = NaniHttpMethod.POST

    val expectedHeaders = Seq(("content-type", "application/json"), ("x-cassandra-token", testConfig.appToken))
    when(
      http(ArgumentMatchers.eq(expectedUrl),
           ArgumentMatchers.eq(expectedHttpMethod),
           ArgumentMatchers.eq(expectedTableDef),
           ArgumentMatchers.eq(expectedHeaders))).thenReturn(apiError)
    assert(driver.make[TestDbThing].error.get.getMessage.equals("\"some code\" - \"some error\""))
  }

  "AstraDriver" should "make: return success" in {
    val (driver, http) = initTest
    val apiResult = """{"name":"test_db_thing"}""".stripMargin
    val expectedUrl =
      "https://test_id-test_region.apps.astra.datastax.com/api/rest/v2/schemas/keyspaces/test_keyspace/tables"
    val expectedTableDef =
      "{\"name\":\"test_db_thing\",\"ifNotExists\":true,\"columnDefinitions\": [ " +
        "{\"name\":\"id\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"data\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"modified\",\"typeDefinition\":\"timestamp\",\"static\":false}]," +
        "\"primaryKey\": {\"partitionKey\":[\"id\"]},\"tableOptions\":{\"defaultTimeToLive\":0}}"
    val expectedHttpMethod = NaniHttpMethod.POST

    val expectedHeaders = Seq(("content-type", "application/json"), ("x-cassandra-token", testConfig.appToken))
    when(
      http(ArgumentMatchers.eq(expectedUrl),
           ArgumentMatchers.eq(expectedHttpMethod),
           ArgumentMatchers.eq(expectedTableDef),
           ArgumentMatchers.eq(expectedHeaders))).thenReturn(apiResult)
    val makeWriteResult = driver.make[TestDbThing]
    assert(makeWriteResult.error.isEmpty)
  }
}

case class TestDbThing() extends DbThing {
  override def keyFields: Seq[Any] = ???
}

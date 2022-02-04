package com.raagnair.nani.drivers.astra

object AstraConnectionConfig {

  val ID_VAR = "ASTRA_DB_ID"
  val REGION_VAR = "ASTRA_DB_REGION"
  val KEYSPACE_VAR = "ASTRA_DB_KEYSPACE"
  val APPLICATION_TOKEN_VAR = "ASTRA_DB_APPLICATION_TOKEN"

  def fromEnvVars: AstraConnectionConfig = {
    AstraConnectionConfig(
      System.getenv(ID_VAR),
      System.getenv(REGION_VAR),
      System.getenv(KEYSPACE_VAR),
      System.getenv(APPLICATION_TOKEN_VAR)
    )
  }
}

case class AstraConnectionConfig(id: String, region: String, keyspace: String, appToken: String)

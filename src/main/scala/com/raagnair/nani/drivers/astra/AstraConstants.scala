package com.raagnair.nani.drivers.astra

object AstraConstants {
  def getTableCreatorJson(name: String): String =
    "{" +
      s"\"name\":\"$name\"," +
      "\"ifNotExists\":true," +
      "\"columnDefinitions\": [ " +
        "{\"name\":\"id\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"data\",\"typeDefinition\":\"text\",\"static\":false}," +
        "{\"name\":\"modified\",\"typeDefinition\":\"timestamp\",\"static\":false}" +
      "]," +
      "\"primaryKey\": {\"partitionKey\":[\"id\"]}," +
      "\"tableOptions\":{\"defaultTimeToLive\":0}" +
    "}"
}

package com.raagnair.nani

import com.raagnair.nani.drivers.astra.{AstraConnectionConfig, AstraDriver}
import com.raagnair.nani.interfaces.DbThing

object Main extends App {
  val driver = AstraDriver()

  println("REAL TEST ENERGY ")
  //println(System.currentTimeMillis())
  //driver.insert(TestThing("id_1", "body_1"))
  //driver.make[TestThingOther]
  driver.make[TestThingOther]
  //driver.insert(TestThingOther("id_1", "body_2"))
  //driver.readAll[TestThingOther].handle(_.printStackTrace()).withMetadata.foreach(println)

  println("Finished.")
}

case class TestThing(id: String, body: String) extends DbThing {
  override def keyFields: Seq[Any] = Seq(id)
}
case class TestThingOther(id: String, body: String) extends DbThing {
  override def keyFields: Seq[Any] = Seq(id)
}

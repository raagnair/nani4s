package com.raagnair.nani.drivers

object NaniMetric {
  def of(start: Long = 0, req: Long = 0, res: Long = 0): NaniMetric = {
    val timeCost = if (start == 0) 0 else System.currentTimeMillis() - start
    NaniMetric(timeCost, req, res)
  }
}
case class NaniMetric(
    queryTimeMs: Long = 0,
    requestBytes: Long = 0,
    responseBytes: Long = 0
)

package com.raagnair.nani.drivers.http

import com.raagnair.nani.drivers.http.NaniHttpMethod.Method
import scalaj.http.{BaseHttp, Http}

object NaniHttpMethod extends Enumeration {
  type Method = Value
  val GET, POST, PUT = Value
}

case class NaniHttp(http: BaseHttp) {
  def apply(url: String, meth: Method, data: String = "", headers: Seq[(String, String)]): String = {
    var baseHttp = http(url)
    if (meth == NaniHttpMethod.POST) baseHttp = baseHttp.postData(data)
    if (meth == NaniHttpMethod.PUT) baseHttp = baseHttp.put(data)
    headers.sortWith((a, b) => a._1.compareTo(b._1) < 0).foreach(h => baseHttp = baseHttp.header(h._1, h._2))
    val rv = baseHttp.asString.body
    rv
  }
}

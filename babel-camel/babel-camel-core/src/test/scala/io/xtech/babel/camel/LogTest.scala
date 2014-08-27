/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.apache.camel.component.mock.MockEndpoint
import org.apache.log4j._
import org.apache.log4j.spi.LoggingEvent
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.immutable

class LogTest extends SpecificationWithJUnit {
  sequential

  "A log" should {

    "ouput correctly" in new camel {

      //#doc:babel-camel-logging

      import io.xtech.babel.camel.builder.RouteBuilder
      import org.apache.camel.LoggingLevel

      val routeBuilder = new RouteBuilder {
        from("direct:input")
          //logs to the Trace level message such as "received ID-3423 -> toto"
          .log(LoggingLevel.TRACE, "my.cool.toto", "foo", "received: ${id} -> ${body}")
          //logs to the Info level message such as "ID-3423 -> toto"
          .log(LoggingLevel.INFO, "${id} -> ${body}")
          .to("mock:output")
      }
      //#doc:babel-camel-logging

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived("babel message")

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val template = camelContext.createProducerTemplate()
      template.sendBody("direct:input", "babel message")
      mock.assertIsSatisfied() must not(throwA[Exception])

      SharedLogs.events.size must be_==(1).eventually

      val event = SharedLogs.events.head
      event.getLevel === Level.TRACE
      event.getLoggerName === "my.cool.toto"
      event.getMessage.toString.startsWith("received") must beTrue
      event.getMessage.toString.endsWith("babel message") must beTrue

    }

  }

}

object SharedLogs {
  var events: immutable.Seq[LoggingEvent] = List.empty[LoggingEvent]
}

class Appender extends AppenderSkeleton {

  def append(event: LoggingEvent) {
    SharedLogs.events :+= event
  }

  def close() {}

  def requiresLayout(): Boolean = false
}

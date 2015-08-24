/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import org.apache.camel.{ Exchange, Processor }
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.specs2.mutable.SpecificationWithJUnit

class MockSpec extends SpecificationWithJUnit {
  sequential

  "camel mock tools" in {

    val camelContext = new DefaultCamelContext()

    import io.xtech.babel.camel.mock._

    val routeDef = new RouteBuilder {
      from("direct:input").
        to("mock:output1").
        process(new Processor {
          override def process(exchange: Exchange): Unit = exchange.getIn.setBody(exchange.getIn.getBody(classOf[String]).toUpperCase)
        }).
        to("mock:output2")

      override def configure(): Unit = {}
    }

    routeDef.addRoutesToCamelContext(camelContext)

    camelContext.start()

    val mockEndpoint1 = camelContext.getMockEndpoint("output1")
    val mockEndpoint2 = camelContext.getMockEndpoint("output2")

    mockEndpoint1.expectedBodiesReceived("test")
    mockEndpoint2.expectedBodiesReceived("TEST")

    val producer = camelContext.createProducerTemplate()

    producer.sendBody("direct:input", "test")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()

    camelContext.shutdown() must not(throwA[Exception])

  }
}


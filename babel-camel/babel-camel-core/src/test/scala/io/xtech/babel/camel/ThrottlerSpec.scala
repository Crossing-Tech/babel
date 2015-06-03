/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.test.camel
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class ThrottlerSpec extends SpecificationWithJUnit {
  sequential

  "throttle message flow" in new camel {

    val msgs = 3

    //#doc:babel-camel-throttler

    val routeDef = new RouteBuilder {
      from("direct:input").
        //throttle message to 1 per second
        throttle(1).
        to("mock:output")
    }
    //#doc:babel-camel-throttler

    val nativeRoute = new CRouteBuilder() {
      def configure() {
        from("direct:inputCamel").
          throttle(1).
          to("mock:output")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(nativeRoute)

    camelContext.start()

    val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

    mockEndpoint.setExpectedMessageCount(msgs)

    val producer = camelContext.createProducerTemplate()

    val initTime = System.currentTimeMillis()
    (1 to msgs).foreach(_ => producer.sendBody("direct:inputCamel", "test"))

    mockEndpoint.assertIsSatisfied()
    (System.currentTimeMillis() - initTime) must be_>=(1000l)

    mockEndpoint.reset()

    mockEndpoint.setExpectedMessageCount(msgs)

    val camelInitTime = System.currentTimeMillis()
    (1 to msgs).foreach(_ => producer.sendBody("direct:input", "test"))

    mockEndpoint.assertIsSatisfied()
    (System.currentTimeMillis() - camelInitTime) must be_>=(1000l)
  }
}

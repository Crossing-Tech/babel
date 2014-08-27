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
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class WireTapTest extends SpecificationWithJUnit {
  sequential

  "create a multicast,aggregate route" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    val routeDef = new RouteBuilder {
      //#doc:babel-camel-wiretap
      from("direct:input-babel").
        //Incoming messages are sent to the direct endpoint
        //   and to the next mock endpoint
        wiretap("direct:babel-tap")
        .to("mock:output-babel")
      //#doc:babel-camel-wiretap

      from("direct:babel-tap").processBody(_ => {
        Thread.sleep(1000);
        "tap"
      }).to("mock:output-babel").to("mock:babel-tap")
    }

    val nativeRoute = new CRouteBuilder() {
      def configure() {
        from("direct:input-camel").wireTap("direct:camel-tap")
          .to("mock:output-camel")

        from("direct:camel-tap").delay(1000).setBody(constant("tap")).to("mock:output-camel").to("mock:camel-tap")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(nativeRoute)

    camelContext.start()

    val mockEndpointB = camelContext.getEndpoint("mock:output-babel").asInstanceOf[MockEndpoint]
    val mockEndpointBT = camelContext.getEndpoint("mock:babel-tap").asInstanceOf[MockEndpoint]
    val mockEndpointC = camelContext.getEndpoint("mock:output-camel").asInstanceOf[MockEndpoint]
    val mockEndpointCT = camelContext.getEndpoint("mock:camel-tap").asInstanceOf[MockEndpoint]

    mockEndpointB.expectedBodiesReceived("test", "tap")
    mockEndpointBT.expectedBodiesReceived("tap")
    mockEndpointC.expectedBodiesReceived("test", "tap")
    mockEndpointCT.expectedBodiesReceived("tap")

    val producer = camelContext.createProducerTemplate()

    producer.sendBody("direct:input-camel", "test")
    producer.sendBody("direct:input-babel", "test")

    mockEndpointC.assertIsSatisfied()
    mockEndpointCT.assertIsSatisfied()
    mockEndpointB.assertIsSatisfied()
    mockEndpointBT.assertIsSatisfied()

  }
}

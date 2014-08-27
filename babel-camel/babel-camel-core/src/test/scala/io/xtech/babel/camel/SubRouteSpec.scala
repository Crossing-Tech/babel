/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class SubRouteSpec extends SpecificationWithJUnit {
  sequential

  "A sub route " should {

    "be set in a route" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-sub

      val routeBuilder = new RouteBuilder {
        from("direct:input").
          to("mock:before").
          //defines a new route called "subroute" which
          //   will send its incoming message to the next mock endpoint
          sub("subroute").
          to("mock:after")
      }
      //#doc:babel-camel-sub

      camelContext.addRoutes(routeBuilder)

      val mockBefore = camelContext.getEndpoint("mock:before", classOf[MockEndpoint])
      mockBefore.expectedBodiesReceived("toto")
      val mockAfter = camelContext.getEndpoint("mock:after", classOf[MockEndpoint])
      mockAfter.expectedBodiesReceived("toto")

      camelContext.start()

      camelContext.createProducerTemplate().sendBody("direct:input", "toto")

      camelContext.getRouteDefinitions.size() === 2

      mockBefore.assertIsSatisfied()
      mockAfter.assertIsSatisfied()

      camelContext.getRouteDefinition("subroute") mustNotEqual null
    }

  }

}

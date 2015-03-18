/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.specs2.mutable.SpecificationWithJUnit

class RouteIdSpec extends SpecificationWithJUnit {
  sequential

  "A route id" should {

    "be set in a route" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-routeId

      val routeBuilder = new RouteBuilder {
        from("direct:input").
          //the routeId of this route will be "bla"
          routeId("bla").
          //the routeId keyword needs to be at the beginning of the route
          //   (enforced by the Babel DSL)
          to("mock:output")
      }
      //#doc:babel-camel-routeId

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      Option(camelContext.getRouteDefinition("bla")) must beSome
    }

    "not be empty" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-routeId-exception-1
      val route = new RouteBuilder {
        from("direct:input").
          //a routeId may not be empty
          routeId("") must throwA[IllegalArgumentException]
      }
      //#doc:babel-camel-routeId-exception-1
    }

    "not be null" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-routeId-exception-2

      val route = new RouteBuilder {
        from("direct:input").
          //a routeId may not be null
          routeId(null) must throwA[IllegalArgumentException]
      }

      //#doc:babel-camel-routeId-exception-2
    }

  }

}

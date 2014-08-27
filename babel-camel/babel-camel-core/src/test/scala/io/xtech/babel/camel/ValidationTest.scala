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
import org.apache.camel.CamelExecutionException
import org.apache.camel.builder.Builder
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class ValidationTest extends SpecificationWithJUnit {
  sequential

  "A validation" should {

    "validate with a predicate" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-validate-1

      val routeBuilder = new RouteBuilder {

        from("direct:input").as[Int].
          validate(Builder.body().isEqualTo(1)).
          to("mock:output")
      }
      //#doc:babel-camel-validate-1

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(1: java.lang.Integer)

      val template = camelContext.createProducerTemplate()

      template.sendBody("direct:input", 1)

      mock.assertIsSatisfied()
    }

    "validate with a function taking a message" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-validate-2

      val routeBuilder = new RouteBuilder {

        from("direct:input").as[Int].
          validate(msg => msg.body == Some(1)).
          to("mock:output")
      }
      //#doc:babel-camel-validate-2

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(1: java.lang.Integer)

      val template = camelContext.createProducerTemplate()

      template.sendBody("direct:input", 1)

      mock.assertIsSatisfied()
    }

    "validate with a function taking a body" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-validate-3

      val routeBuilder = new RouteBuilder {

        from("direct:input").as[Int].
          validateBody(body => body == 1).
          to("mock:output")
      }
      //#doc:babel-camel-validate-3

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(1: java.lang.Integer)

      val template = camelContext.createProducerTemplate()

      template.sendBody("direct:input", 1)

      mock.assertIsSatisfied()
    }

    "through an exception if the message is not valid" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeBuilder = new RouteBuilder {

        from("direct:input").as[Int].
          validateBody(body => body == 123).
          to("mock:output")
      }

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      val template = camelContext.createProducerTemplate()

      template.sendBody("direct:input", 1) must throwAn[CamelExecutionException]
    }
  }
}

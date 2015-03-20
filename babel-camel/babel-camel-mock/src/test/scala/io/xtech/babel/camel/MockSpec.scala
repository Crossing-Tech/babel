/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.test._
import org.specs2.mutable.SpecificationWithJUnit

class MockSpec extends SpecificationWithJUnit {
  sequential

  "extend the DSL with some sub DSL (mock)" in new camel {

    //#doc:babel-camel-mock
    import io.xtech.babel.camel.mock._

    //The Mock extension is added simply by
    //  extending the RouteBuilder with
    val routeDef = new RouteBuilder with Mock {
      //the mock keyword is the same as typing
      //  to("mock:output")
      from("direct:input").mock("output")
    }

    //#doc:babel-camel-mock

    routeDef.addRoutesToCamelContext(camelContext)

    camelContext.start()

    val mockEndpoint = camelContext.getMockEndpoint("output")

    mockEndpoint.expectedBodiesReceived("test")

    val producer = camelContext.createProducerTemplate()

    producer.sendBody("direct:input", "test")

    mockEndpoint.assertIsSatisfied()

  }
}


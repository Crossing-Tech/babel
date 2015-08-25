/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.specs2.mutable.SpecificationWithJUnit

class MockSpec extends SpecificationWithJUnit {
  sequential

  "extend the DSL with some sub DSL (mock)" in {

    val camelContext = new DefaultCamelContext()

    //#doc:babel-camel-mock
    import io.xtech.babel.camel.mock._

    //The Mock extension is added simply by
    //  extending the RouteBuilder with
    val routeDef = new RouteBuilder with Mock {
      //the mock keyword is the same as typing
      //  to("mock:output1")
      from("direct:input").
        requireAs[String].
        mock("output1").
        //the mock keyword keeps the same body type (here: String)
        processBody(x => x.toUpperCase).
        mock("output2")

    }

    //#doc:babel-camel-mock

    routeDef.addRoutesToCamelContext(camelContext)

    camelContext.start()

    val mockEndpoint1 = camelContext.mockEndpoint("output1")
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


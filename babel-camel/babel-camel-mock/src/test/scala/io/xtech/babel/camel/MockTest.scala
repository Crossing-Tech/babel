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

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.test._

import org.apache.camel.impl.{DefaultExchange, DefaultCamelContext}
import org.specs2.mutable.{After, SpecificationWithJUnit}

class MockTest extends SpecificationWithJUnit {
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


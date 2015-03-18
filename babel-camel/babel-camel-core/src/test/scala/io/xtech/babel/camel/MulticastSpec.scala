/*
 *
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

class MulticastSpec extends SpecificationWithJUnit {
  sequential

  "create a multicast,aggregate route" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-multicast

    val routeDef = new RouteBuilder {
      from("direct:input").as[String].
        //received messages are sent to those three mock endpoints
        multicast("mock:output1", "mock:output2", "mock:output3").
        to("mock:output4")
    }
    //#doc:babel-camel-multicast

    val nativeRoute = new CRouteBuilder() {
      def configure(): Unit = {
        from("direct:inputCamel").
          multicast().to("mock:output1", "mock:output2", "mock:output3").
          to("mock:output4")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(nativeRoute)

    camelContext.start()

    val mockEndpoint1 = camelContext.getEndpoint("mock:output1").asInstanceOf[MockEndpoint]
    val mockEndpoint2 = camelContext.getEndpoint("mock:output2").asInstanceOf[MockEndpoint]
    val mockEndpoint3 = camelContext.getEndpoint("mock:output3").asInstanceOf[MockEndpoint]
    val mockEndpoint4 = camelContext.getEndpoint("mock:output4").asInstanceOf[MockEndpoint]

    mockEndpoint1.expectedBodiesReceived("test")
    mockEndpoint2.expectedBodiesReceived("test")
    mockEndpoint3.expectedBodiesReceived("test")
    mockEndpoint4.expectedBodiesReceived("test")

    val producer = camelContext.createProducerTemplate()

    producer.sendBody("direct:inputCamel", "test")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()

    mockEndpoint1.reset()
    mockEndpoint2.reset()
    mockEndpoint3.reset()
    mockEndpoint4.reset()

    producer.sendBody("direct:input", "test")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()
  }
}

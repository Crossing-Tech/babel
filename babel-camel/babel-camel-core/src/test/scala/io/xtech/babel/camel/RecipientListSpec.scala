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
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder, ValueBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.language.HeaderExpression
import org.specs2.mutable.SpecificationWithJUnit

class RecipientListSpec extends SpecificationWithJUnit {
  sequential

  "create a recipientList route with a function" in new camel {
    //#doc:babel-camel-recipientList
    val routeDef = new RouteBuilder {
      from("direct:input").as[String].
        //received messages targets are dynamically defined by the headers of each message
        recipientList(m => m.headers("recipients")).
        to("mock:output4")
    }
    //#doc:babel-camel-recipientList

    val nativeRoute = new CRouteBuilder() {
      def configure(): Unit = {
        from("direct:inputCamel").
          recipientList(header("recipients")).
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

    producer.sendBodyAndHeader("direct:inputCamel", "test", "recipients", "mock:output1,mock:output2,mock:output3")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()

    mockEndpoint1.reset()
    mockEndpoint2.reset()
    mockEndpoint3.reset()
    mockEndpoint4.reset()

    producer.sendBodyAndHeader("direct:input", "test", "recipients", "mock:output1,mock:output2,mock:output3")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()
  }

  "create a recipientList route with an expression" in new camel {

    val routeDef = new RouteBuilder {
      from("direct:input").as[String].
        recipientList(new ValueBuilder(new HeaderExpression("recipients"))). //received messages targets are dynamically defined by the headers of each message, based on some Camel Expression language
        to("mock:output4")
    }

    val nativeRoute = new CRouteBuilder() {
      def configure(): Unit = {
        from("direct:inputCamel").
          recipientList(header("recipients")).
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

    producer.sendBodyAndHeader("direct:inputCamel", "test", "recipients", "mock:output1,mock:output2,mock:output3")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()

    mockEndpoint1.reset()
    mockEndpoint2.reset()
    mockEndpoint3.reset()
    mockEndpoint4.reset()

    producer.sendBodyAndHeader("direct:input", "test", "recipients", "mock:output1,mock:output2,mock:output3")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()
  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.splitfilter

import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.RouteDefinitionException
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class SimpleSplitFilterSpec extends SpecificationWithJUnit {
  "split and filter" should {
    "integrates together in a route" in new camel {
      //#doc:babel-camel-filter

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //direct:input receives one message with
        //   "1,2,3,true,4,5,6,7"
        from("direct:input").as[String]
          //the splitBody creates 8 messages with string body
          //   by a function which returns a string iterator
          .splitBody(body => body.split(",").iterator)
          //the filter will only let continue the one whose body is "true"
          .filter(msg => msg.body.exists(body => body == "true")).
          to("mock:output")
      }
      //#doc:babel-camel-filter

      val otherRoute = new CRouteBuilder() {
        def configure(): Unit = {
          from("direct:camel").split(body.tokenize(",")).filter(body.contains("true")).to("mock:camel")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(otherRoute)
      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("true")
      mockCamel.expectedBodiesReceived("true")

      producer.sendBody("direct:input", "1,2,3,true,4,5,6,7")
      producer.sendBody("direct:camel", "1,2,3,true,4,5,6,7")

      mockEndpoint.assertIsSatisfied()
    }
    "integrates together in a route using body functions" in new camel {
      //#doc:babel-camel-filter

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //direct:input receives one message with
        //   "1,2,3,true,4,5,6,7"
        from("direct:input").as[String]
          //the splitBody creates 8 messages with string body
          //   by a function which returns a string iterator
          .splitBody(body => body.split(",").iterator)
          //the filter will only let continue the one whose body is "true"
          .filterBody(body => body == "true").
          to("mock:output")
      }
      //#doc:babel-camel-filter

      val otherRoute = new CRouteBuilder() {
        def configure(): Unit = {
          from("direct:camel").split(body.tokenize(",")).filter(body.contains("true")).to("mock:camel")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(otherRoute)
      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("true")
      mockCamel.expectedBodiesReceived("true")

      producer.sendBody("direct:input", "1,2,3,true,4,5,6,7")
      producer.sendBody("direct:camel", "1,2,3,true,4,5,6,7")

      mockEndpoint.assertIsSatisfied()
    }
    "integrates together in a complex route" in new camel {

      //#doc:babel-camel-splitter

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").as[String]
          //split the message base on its body using the comma
          .splitBody(_.split(",").iterator)
          //which creates several messages
          .filter(msg => msg.body.exists(body => body.contains("true")))
          //required as keyword to recover the type
          .to("mock:output").as[String]
          //split the message base on its body using the spaces
          .splitBody(_.split(" ").iterator)
          .filter(msg => msg.body.exists(body => body == "false")).to("mock:false")
      }
      //#doc:babel-camel-splitter

      val otherRoute = new CRouteBuilder() {
        def configure(): Unit = {
          from("direct:camel").split(body.tokenize(",")).filter(body.contains("true")).to("mock:camel")
            .split(body.tokenize(" ")).filter(body.isEqualTo("false")).to("mock:camelfalse")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(otherRoute)
      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      val mockEndEndpoint = camelContext.getEndpoint("mock:false").asInstanceOf[MockEndpoint]
      val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]
      val mockEndCamel = camelContext.getEndpoint("mock:camelfalse").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("true false")
      mockEndEndpoint.expectedBodiesReceived("false")
      mockCamel.expectedBodiesReceived("true false")
      mockEndCamel.expectedBodiesReceived("false")

      producer.sendBody("direct:input", "1 2,2 3,3 4,true false")
      producer.sendBody("direct:camel", "1 2,2 3,3 4,true false")

      mockCamel.assertIsSatisfied()
      mockEndpoint.assertIsSatisfied()
      mockEndCamel.assertIsSatisfied()
      mockEndEndpoint.assertIsSatisfied()
    }
    "a route ending with filter should be built" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").as[String].filter(_.body.exists(_ == "false"))
      }

      routeDef.build should not(throwA[RouteDefinitionException])
    }
  }
}

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
import io.xtech.babel.fish.RouteDefinitionException
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.{ Exchange, ExchangePattern, Processor }
import org.specs2.mutable.SpecificationWithJUnit

class CamelDSLSpec extends SpecificationWithJUnit {
  sequential

  "CamelDSL" should {

    "create a route with an empty DSL" in new camel {

      val routeDef = new RouteBuilder {
        from("direct:input")
      }.build() must throwA[RouteDefinitionException]
    }

    "create a from,endpoint route" in new camel {

      //#doc:babel-camel-example
      //#doc:babel-camel-basic-1

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //sends what is received in the direct
        //  endpoint to the mock endpoint
        from("direct:input").to("mock:output")
      }
      //#doc:babel-camel-example
      //#doc:babel-camel-basic-1

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEndpoint.expectedBodiesReceived("bla")

      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "create a from,endpoint route with inOnly exchange pattern" in new camel {

      //#doc:babel-camel-basic-2

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //the mock endpoint is set in InOnly Exchange Pattern
        from("direct:input").to("mock:output", false)
      }
      //#doc:babel-camel-basic-2
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEndpoint.expectedBodiesReceived("bla")
      mockEndpoint.expectedExchangePattern(ExchangePattern.InOnly)

      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "create a from,endpoint route with inOut exchange pattern" in new camel {

      //#doc:babel-camel-basic-3

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //the mock endpoint is set in InOut Exchange Pattern
        from("direct:input").to("mock:output", true)
      }
      //#doc:babel-camel-basic-3
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEndpoint.expectedBodiesReceived("bla")
      mockEndpoint.expectedExchangePattern(ExchangePattern.InOut)

      producer.requestBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "create a from,process,endpoint route" in new camel {
      //#doc:babel-camel-processBody-1

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //message bodies are converted to String if required
        from("direct:input").as[String].
          //processBody concatenates received string with "bli"
          processBody(string => string + "bli").
          //sends the concatenated string to the mock endpoint
          to("mock:output")
      }
      //#doc:babel-camel-processBody-1
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEnpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEnpoint.expectedBodiesReceived("blablibli")

      producer.sendBody("direct:input", "blabli")

      mockEnpoint.assertIsSatisfied()
    }

    "create a from,processMessage,endpoint route" in new camel {
      //#doc:babel-camel-process-1

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //message bodies are converted to String if required
        from("direct:input").as[String].
          //process redefines a new Message with Body
          process(msg => msg.withBody(_ + "bli")).
          //sends the concatenated string to the mock endpoints
          to("mock:output")
      }
      //#doc:babel-camel-process-1

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEnpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEnpoint.expectedBodiesReceived("blablibli")

      producer.sendBody("direct:input", "blabli")

      mockEnpoint.assertIsSatisfied()
    }

    "create a processor with processorId" in new camel {

      val routeDef = new RouteBuilder {
        from("direct:input").routeId("babel")
          .process(msg => msg.withBody(_ + "bli"), "toto-babel")
          .to("mock:output")
      }

      val route = new org.apache.camel.builder.RouteBuilder() {
        override def configure(): Unit = {
          from("direct:camel").routeId("camel")
            .process(new Processor {
              override def process(p1: Exchange): Unit = {
                println("toto")
              }
            }).id("toto-camel")
            .to("mock:camel")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(route)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEnpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEnpoint.expectedBodiesReceived("blablibli")

      producer.sendBody("direct:input", "blabli")

      camelContext.getRouteDefinition("camel").getOutputs.get(0).getId === "toto-camel"
      camelContext.getRouteDefinition("babel").getOutputs.get(0).getId === "toto-babel"

      mockEnpoint.assertIsSatisfied()
    }
  }

  "Camel Message" should {
    "allows to modify exchange property" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //sends what is received in the direct endpoint to the mock endpoint
        from("direct:input").
          process(_.withExchangeProperty("babel", "toto")).
          to("mock:output")
      }

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      producer.sendBody("direct:input", "bla")

      mockEndpoint.getReceivedExchanges.get(0).getProperties.get("babel").toString === "toto"
    }

    "allows to read and modify exchange properties" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //sends what is received in the direct endpoint to the mock endpoint
        from("direct:input").
          process(msg => {
            assert(msg.exchangeProperties.contains("babel"))
            msg.withExchangeProperties((_) => Map("babel" -> "toto"))
          }).
          to("mock:output")
      }

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      val exchange = new DefaultExchange(camelContext)
      exchange.setProperty("babel", "bli")
      producer.send("direct:input", exchange)

      mockEndpoint.getReceivedExchanges.get(0).getProperties.get("babel").toString === "toto"
    }

    "allows to read and modify exchange exception" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //sends what is received in the direct endpoint to the mock endpoint
        from("direct:input").
          process(msg => {
            assert(msg.exchangeException == null)
            msg.withExchangeException(new Exception("Expected exception"))
          }).
          to("mock:output")
      }

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      producer.sendBody("direct:input", "bla") must throwA[Exception]

    }

    "allows to read and modify exchange pattern" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //sends what is received in the direct endpoint to the mock endpoint
        from("direct:input").
          process(msg => {
            assert(msg.exchangePattern.isInstanceOf[ExchangePattern])
            msg.withExchangePattern(ExchangePattern.InOptionalOut)
          }).
          to("mock:output")
      }

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      producer.sendBody("direct:input", "bla")

      mockEndpoint.getReceivedExchanges.get(0).getPattern === ExchangePattern.InOptionalOut
    }
  }
}

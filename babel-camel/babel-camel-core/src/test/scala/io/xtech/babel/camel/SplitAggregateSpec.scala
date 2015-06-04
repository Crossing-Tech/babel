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
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.{ Exchange, Processor }
import org.specs2.mutable.SpecificationWithJUnit

class SplitAggregateSpec extends SpecificationWithJUnit {
  sequential

  "create a route with an split reduce" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitReduceBody(_.split(",").iterator) {
        _.to("mock:babel1").requireAs[String].processBody(_.toInt + 1 + "").to("mock:babel2").requireAs[String]
      }((x, y) => s"$x,$y").
        processBody(x => x).
        to("mock:babel3")
    }
    //#doc:babel-camel-split-reduce

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(","), new AggregationStrategy {
          override def aggregate(exchange: Exchange, exchange1: Exchange): Exchange = {
            if (exchange == null) {
              //first exchange
              exchange1
            }
            else {
              exchange.getIn.setBody(exchange.getIn.getBody + "," + exchange1.getIn.getBody)
              exchange
            }
          }
        }).to("mock:camel1").process(new Processor {
          override def process(exchange: Exchange): Unit = {
            exchange.getIn.setBody(exchange.getIn.getBody(classOf[String]).toInt + 1 + "")
          }
        }).to("mock:camel2").end().to("mock:camel3")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.getEndpoint("mock:babel1").asInstanceOf[MockEndpoint]
    val mockBabel2 = camelContext.getEndpoint("mock:babel2").asInstanceOf[MockEndpoint]
    val mockBabel3 = camelContext.getEndpoint("mock:babel3").asInstanceOf[MockEndpoint]
    mockBabel1.expectedBodiesReceived("1", "2", "3")
    mockBabel2.expectedBodiesReceived("2", "3", "4")
    mockBabel3.expectedBodiesReceived("2,3,4")

    val mockCamel1 = camelContext.getEndpoint("mock:camel1").asInstanceOf[MockEndpoint]
    val mockCamel2 = camelContext.getEndpoint("mock:camel2").asInstanceOf[MockEndpoint]
    val mockCamel3 = camelContext.getEndpoint("mock:camel3").asInstanceOf[MockEndpoint]
    mockCamel1.expectedBodiesReceived("1", "2", "3")
    mockCamel2.expectedBodiesReceived("2", "3", "4")
    mockCamel3.expectedBodiesReceived("2,3,4")

    producer.sendBody("direct:babel", "1,2,3")
    producer.sendBody("direct:camel", "1,2,3")

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockCamel3.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()
    mockBabel2.assertIsSatisfied()
    mockBabel3.assertIsSatisfied()

  }

  "create a route with an split fold" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder
    //#doc:babel-camel-split-fold

    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitFoldBody(_.split(",").iterator) {
        _.to("mock:babel1").requireAs[String].processBody(_.toInt + 1).to("mock:babel2").requireAs[Int]
      }("1")((x, y) => s"$x,$y").
        processBody(x => x).
        to("mock:babel3")
    }
    //#doc:babel-camel-split-fold

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(","), new AggregationStrategy {
          override def aggregate(exchange: Exchange, exchange1: Exchange): Exchange = {
            if (exchange == null) {
              //first exchange
              exchange1.getIn().setBody("1," + exchange1.getIn.getBody())
              exchange1
            }
            else {
              exchange.getIn.setBody(exchange.getIn.getBody + "," + exchange1.getIn.getBody)
              exchange
            }
          }
        }).to("mock:camel1").process(new Processor {
          override def process(exchange: Exchange): Unit = {
            exchange.getIn.setBody(exchange.getIn.getBody(classOf[String]).toInt + 1 + "")
          }
        }).to("mock:camel2").end().to("mock:camel3")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.getEndpoint("mock:babel1").asInstanceOf[MockEndpoint]
    val mockBabel2 = camelContext.getEndpoint("mock:babel2").asInstanceOf[MockEndpoint]
    val mockBabel3 = camelContext.getEndpoint("mock:babel3").asInstanceOf[MockEndpoint]
    mockBabel1.expectedBodiesReceived("1", "2", "3")
    mockBabel2.expectedBodiesReceived("2", "3", "4")
    mockBabel3.expectedBodiesReceived("1,2,3,4")

    val mockCamel1 = camelContext.getEndpoint("mock:camel1").asInstanceOf[MockEndpoint]
    val mockCamel2 = camelContext.getEndpoint("mock:camel2").asInstanceOf[MockEndpoint]
    val mockCamel3 = camelContext.getEndpoint("mock:camel3").asInstanceOf[MockEndpoint]
    mockCamel1.expectedBodiesReceived("1", "2", "3")
    mockCamel2.expectedBodiesReceived("2", "3", "4")
    mockCamel3.expectedBodiesReceived("1,2,3,4")

    producer.sendBody("direct:babel", "1,2,3")
    producer.sendBody("direct:camel", "1,2,3")

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockCamel3.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()
    mockBabel2.assertIsSatisfied()
    mockBabel3.assertIsSatisfied()

  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.model.Aggregation.{ CamelAggregation, CompletionSize, ReduceBody, _ }
import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.MessageExpression
import io.xtech.babel.fish.model.Message
import org.apache.camel.Exchange
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.processor.aggregate.{ AggregationStrategy, GroupedExchangeAggregationStrategy }
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.JavaConverters._

class AggregateSpec extends SpecificationWithJUnit {
  sequential

  "create a route with an aggregate eip the camel way" in new camel {
    //#doc:babel-camel-aggregate-camel-1
    import io.xtech.babel.camel.builder.RouteBuilder
    import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy
    val camelAggr = CamelAggregation(MessageExpression((msg: Message[String]) => "1"),
      aggregationStrategy = new GroupedExchangeAggregationStrategy,
      completionStrategies = List(CompletionSize(3)))

    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:input").as[String].
        //aggregates strings based on the camelAggr defined higher
        aggregate(camelAggr).
        //sends the aggregated string to the mock endpoint
        to("mock:output")
    }
    //#doc:babel-camel-aggregate-camel-1

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").aggregate(constant("1"), new GroupedExchangeAggregationStrategy).completionSize(3).to("mock:camel")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
    mockEndpoint.expectedMessageCount(1)
    val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]
    mockCamel.expectedMessageCount(1)

    producer.sendBody("direct:input", "1")
    producer.sendBody("direct:input", "2")
    producer.sendBody("direct:input", "3")

    producer.sendBody("direct:camel", "1")
    producer.sendBody("direct:camel", "2")
    producer.sendBody("direct:camel", "3")

    mockEndpoint.assertIsSatisfied()
    mockCamel.assertIsSatisfied()

    val List(receivedExchanges, camelExchanges) = List(mockEndpoint, mockCamel).map(_.getReceivedExchanges.asScala)
    val List(groupedExchanges, camelGroupedExchanges) = List(receivedExchanges, camelExchanges).map(x => x.map(ex => ex.getProperty(Exchange.GROUPED_EXCHANGE, classOf[java.util.List[Exchange]]).asScala).flatten)
    val List(bodies, camelBodies) = List(groupedExchanges, camelGroupedExchanges).map(x => x.map(_.getIn.getBody))

    bodies must contain("1", "2", "3")
    camelBodies must contain("1", "2", "3")
  }

  "create a route with an aggregate eip the camel way and a aggregation strategy as a bean" in new camel {

    //defines how the exchange should be aggregated
    val aggregationStrategy = new GroupedExchangeAggregationStrategy

    //#doc:babel-camel-aggregate-camel-2
    //defines when message should be aggregated
    val camelExp = new MessageExpression((a: Message[String]) => "1")

    import io.xtech.babel.camel.model.Aggregation.CamelReferenceAggregation
    val camelAggr = CamelReferenceAggregation[String, String](
      correlationExpression = camelExp,
      //defines the string id of the aggregation strategy in the bean registry
      "aggregationStrategy",
      completionStrategies = List(CompletionSize(3)))

    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:input").as[String].
        //aggregates strings based on the camelAggr defined higher
        aggregate(camelAggr).
        //sends the aggregated string to the mock endpoint
        to("mock:output")
    }
    //#doc:babel-camel-aggregate-camel-2

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").aggregate(constant("1")).aggregationStrategyRef("aggregationStrategy").completionSize(3).to("mock:camel")
      }
    }

    val registry = new SimpleRegistry
    registry.put("aggregationStrategy", aggregationStrategy)
    camelContext.setRegistry(registry)

    routeDef.addRoutesToCamelContext(camelContext)

    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
    mockEndpoint.expectedMessageCount(1)
    val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]
    mockCamel.expectedMessageCount(1)

    producer.sendBody("direct:input", "1")
    producer.sendBody("direct:input", "2")
    producer.sendBody("direct:input", "3")

    producer.sendBody("direct:camel", "1")
    producer.sendBody("direct:camel", "2")
    producer.sendBody("direct:camel", "3")

    mockEndpoint.assertIsSatisfied()
    mockCamel.assertIsSatisfied()

    val List(receivedExchanges, camelExchanges) = List(mockEndpoint, mockCamel).map(_.getReceivedExchanges.asScala)
    val List(groupedExchanges, camelGroupedExchanges) = List(receivedExchanges, camelExchanges).map(x => x.map(ex => ex.getProperty(Exchange.GROUPED_EXCHANGE, classOf[java.util.List[Exchange]]).asScala).flatten)
    val List(bodies, camelBodies) = List(groupedExchanges, camelGroupedExchanges).map(x => x.map(_.getIn.getBody))

    bodies must contain("1", "2", "3")
    camelBodies must contain("1", "2", "3")
  }

  "create a route with an aggregate eip with a body reduce" in new camel {
    //#doc:babel-camel-aggregate-reduce
    // inputs (1,2,3,4,5,6,7,8,9) -> outputs (6,15,24)
    val reduceBody = ReduceBody(
      //defines how message bodies are aggregated
      reduce = (a: Int, b: Int) => a + b,
      //defines when message may be aggregated
      groupBy = (msg: Message[Int]) => "a",
      //defines the size of the aggregation (3 messages)
      completionStrategies = List(CompletionSize(3)))

    import io.xtech.babel.camel.builder.RouteBuilder

    val routeDef = new RouteBuilder {
      from("direct:babel").as[Int].
        aggregate(reduceBody).
        to("mock:output")
    }
    //#doc:babel-camel-aggregate-reduce

    val camelRoute = new CRouteBuilder() {
      val strategy = new AggregationStrategy {
        def aggregate(oldExchange: Exchange, newExchange: Exchange): Exchange = {
          Option(oldExchange) match {
            case Some(exchange) =>
              newExchange.getIn.setBody(exchange.getIn.getBody(classOf[Int]) + newExchange.getIn.getBody(classOf[Int]))
              newExchange
            case None => newExchange
          }
        }
      }

      def configure(): Unit = {
        from("direct:input").multicast().to("direct:babel").to("direct:camel").end
        from("direct:camel").aggregate(constant("a"), strategy).completionSize(3).to("mock:camel")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
    val camelEndpoint = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]

    mockEndpoint.expectedBodiesReceived(6: Integer, 15: Integer, 24: Integer)
    camelEndpoint.expectedBodiesReceived(6: Integer, 15: Integer, 24: Integer)

    producer.sendBody("direct:input", 1)
    producer.sendBody("direct:input", 2)
    producer.sendBody("direct:input", 3)

    producer.sendBody("direct:input", 4)
    producer.sendBody("direct:input", 5)
    producer.sendBody("direct:input", 6)

    producer.sendBody("direct:input", 7)
    producer.sendBody("direct:input", 8)
    producer.sendBody("direct:input", 9)

    mockEndpoint.assertIsSatisfied()
    camelEndpoint.assertIsSatisfied()
  }

  "create a route with an aggregate eip with a body fold" in new camel {
    //#doc:babel-camel-aggregate-fold
    // inputs (1,2,3,4,5,6,7,8,9) -> outputs ("123","456","789")
    val foldBody = FoldBody("",
      //defines how message bodies are aggregated
      (a: String, b: Int) => a + b,
      //defines when message may be aggregated
      (msg: Message[Int]) => "a",
      //defines the size of the aggregation (3 messages)
      completionStrategies = List(CompletionSize(3)))

    import io.xtech.babel.camel.builder.RouteBuilder

    val routeDef = new RouteBuilder {
      from("direct:babel").as[Int].
        aggregate(foldBody).
        to("mock:output")
    }
    //#doc:babel-camel-aggregate-fold

    val camelRoute = new CRouteBuilder() {
      val strategy = new AggregationStrategy {
        def aggregate(oldExchange: Exchange, newExchange: Exchange): Exchange = {
          Option(oldExchange) match {
            case Some(exchange) =>
              newExchange.getIn.setBody(exchange.getIn.getBody(classOf[String]) + newExchange.getIn.getBody(classOf[Int]))
              newExchange
            case None =>
              newExchange.getIn.setBody(newExchange.getIn.getBody(classOf[String]))
              newExchange
          }
        }
      }

      def configure(): Unit = {
        from("direct:input").multicast().to("direct:babel").to("direct:camel").end
        from("direct:camel").aggregate(constant("a"), strategy).completionSize(3).to("mock:camel")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
    val mockCamel = camelContext.getEndpoint("mock:camel").asInstanceOf[MockEndpoint]

    mockEndpoint.expectedBodiesReceived("123", "456", "789")
    mockCamel.expectedBodiesReceived("123", "456", "789")

    producer.sendBody("direct:input", 1)
    producer.sendBody("direct:input", 2)
    producer.sendBody("direct:input", 3)

    producer.sendBody("direct:input", 4)
    producer.sendBody("direct:input", 5)
    producer.sendBody("direct:input", 6)

    producer.sendBody("direct:input", 7)
    producer.sendBody("direct:input", 8)
    producer.sendBody("direct:input", 9)

    mockEndpoint.assertIsSatisfied()
    mockCamel.assertIsSatisfied()
  }

}

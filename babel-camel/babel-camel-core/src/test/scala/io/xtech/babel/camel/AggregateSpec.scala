/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.mock._
import io.xtech.babel.camel.model.Aggregation.{ CamelAggregation, CompletionSize, ReduceBody, _ }
import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.MessageExpression
import io.xtech.babel.fish.model.Message
import org.apache.camel.Exchange
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.processor.aggregate.{ AggregationStrategy, GroupedExchangeAggregationStrategy }
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.JavaConverters._

class AggregateSpec extends SpecificationWithJUnit {
  sequential

  private val directConsumer = "direct:input"

  "create a route with an aggregate eip the camel way" in new camel {
    //#doc:babel-camel-aggregate-camel-1

    import io.xtech.babel.camel.builder.RouteBuilder
    import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy

    val camelAggr = CamelAggregation(MessageExpression((msg: Message[String]) => "1"),
      aggregationStrategy = new GroupedExchangeAggregationStrategy,
      completionStrategies = List(CompletionSize(3), CompletionInterval(1000)))

    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from(directConsumer).as[String].
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

    val mockEndpoint = camelContext.mockEndpoint("output")
    mockEndpoint.expectedMessageCount(1)
    val mockCamel = camelContext.mockEndpoint("camel")
    mockCamel.expectedMessageCount(1)

    producer.sendBody(directConsumer, "1")
    producer.sendBody(directConsumer, "2")
    producer.sendBody(directConsumer, "3")

    producer.sendBody("direct:camel", "1")
    producer.sendBody("direct:camel", "2")
    producer.sendBody("direct:camel", "3")

    mockEndpoint.assertIsSatisfied()
    mockCamel.assertIsSatisfied()

    val List(receivedExchanges, camelExchanges) = List(mockEndpoint, mockCamel).map(_.getReceivedExchanges.asScala)
    val List(groupedExchanges, camelGroupedExchanges) = List(receivedExchanges, camelExchanges).
      map(x => x.map(ex => ex.getProperty(Exchange.GROUPED_EXCHANGE, classOf[java.util.List[Exchange]]).asScala).flatten)

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
      from(directConsumer).as[String].
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

    val mockEndpoint = camelContext.mockEndpoint("output")
    mockEndpoint.expectedMessageCount(1)
    val mockCamel = camelContext.mockEndpoint("camel")
    mockCamel.expectedMessageCount(1)

    producer.sendBody(directConsumer, "1")
    producer.sendBody(directConsumer, "2")
    producer.sendBody(directConsumer, "3")

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
      completionStrategies = List(CompletionSize(3), CompletionTimeout(1000), ForceCompletionOnStop))

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
        from(directConsumer).multicast().to("direct:babel").to("direct:camel").end
        from("direct:camel").aggregate(constant("a"), strategy).completionSize(3).to("mock:camel")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.mockEndpoint("output")
    val camelEndpoint = camelContext.mockEndpoint("camel")

    mockEndpoint.expectedBodiesReceived(6: Integer, 15: Integer, 24: Integer)
    camelEndpoint.expectedBodiesReceived(6: Integer, 15: Integer, 24: Integer)

    producer.sendBody(directConsumer, 1)
    producer.sendBody(directConsumer, 2)
    producer.sendBody(directConsumer, 3)

    producer.sendBody(directConsumer, 4)
    producer.sendBody(directConsumer, 5)
    producer.sendBody(directConsumer, 6)

    producer.sendBody(directConsumer, 7)
    producer.sendBody(directConsumer, 8)
    producer.sendBody(directConsumer, 9)

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
      completionStrategies = List(CompletionSize(3), CompletionFromBatchConsumer))

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
              newExchange.getIn.setBody(exchange.getIn.getBody(classOf[String]) +
                newExchange.getIn.getBody(classOf[Int]))
              newExchange
            case None =>
              newExchange.getIn.setBody(newExchange.getIn.getBody(classOf[String]))
              newExchange
          }
        }
      }

      def configure(): Unit = {
        from(directConsumer).multicast().to("direct:babel").to("direct:camel").end
        from("direct:camel").aggregate(constant("a"), strategy).completionSize(3).to("mock:camel")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint = camelContext.mockEndpoint("output")
    val mockCamel = camelContext.mockEndpoint("camel")

    mockEndpoint.expectedBodiesReceived("123", "456", "789")
    mockCamel.expectedBodiesReceived("123", "456", "789")

    producer.sendBody(directConsumer, 1)
    producer.sendBody(directConsumer, 2)
    producer.sendBody(directConsumer, 3)

    producer.sendBody(directConsumer, 4)
    producer.sendBody(directConsumer, 5)
    producer.sendBody(directConsumer, 6)

    producer.sendBody(directConsumer, 7)
    producer.sendBody(directConsumer, 8)
    producer.sendBody(directConsumer, 9)

    mockEndpoint.assertIsSatisfied()
    mockCamel.assertIsSatisfied()
  }

}

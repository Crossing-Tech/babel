/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.choice

import io.xtech.babel.camel.model.Aggregation.{ CompletionSize, ReduceBody }
import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.model.Message
import org.apache.camel.Exchange
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.specs2.mutable.SpecificationWithJUnit

class DemoSpec extends SpecificationWithJUnit {

  "demo route" in new camel {

    sequential

    //#doc:babel-camel-demo-2
    val doAggregate = ReduceBody(
      //defines how message bodies are aggregated
      (a: Int, b: Int) => a + b,
      //defines when message may be aggregated
      (msg: Message[Int]) => "a",
      //defines the size of the aggregation (3 messages)
      completionStrategies = List(CompletionSize(3))
    )

    //#doc:babel-camel-demo-2

    //#doc:babel-camel-demo-1

    import io.xtech.babel.camel.builder.RouteBuilder

    val myRoute = new RouteBuilder {
      from("direct:babel").requireAs[String].
        splitBody(_.split(",").map(_.toInt).iterator).

        aggregate(doAggregate).

        choice { c =>
          c.whenBody(_ > 0).
            to("mock:database")

          c.whenBody(_ < 0).
            processBody(int => s"$int is negative").
            to("mock:error")
        }
    }
    //#doc:babel-camel-demo-1

    myRoute.addRoutesToCamelContext(camelContext)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint1 = camelContext.getEndpoint("mock:database").asInstanceOf[MockEndpoint]
    val mockEndpoint2 = camelContext.getEndpoint("mock:error").asInstanceOf[MockEndpoint]

    mockEndpoint1.expectedBodiesReceived("6")
    mockEndpoint2.expectedBodiesReceived("-1 is negative")

    producer.sendBody("direct:babel", "1,2,3")
    producer.sendBody("direct:babel", "1,2,-4")

    mockEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()

  }

  "demo camel-scalaroute" in new camel {
    sequential

    //#doc:babel-camel-demo-scala-2
    val aggregationStrategy = new AggregationStrategy {
      def aggregate(old: Exchange, news: Exchange) =
        (old, news) match {
          case (old, null)  => old
          case (null, news) => news
          case (old, news) =>
            old.getIn.setBody(
              old.getIn.getBody(classOf[Int]) +
                news.getIn.getBody(classOf[Int]))
            old
        }
    }
    //#doc:babel-camel-demo-scala-2

    //#doc:babel-camel-demo-scala-1

    import org.apache.camel.scala.dsl.builder.RouteBuilder

    val routing = new RouteBuilder {
      from("direct:camel-scala").
        split(msg => msg.in[String].split(",")).

        aggregate("a", aggregationStrategy).completionSize(3).

        choice {
          when(_.in[Int] > 0).
            to("mock:database-camel-scala")

          when(_.in[Int] < 0).
            process(msg => msg.in = s"${msg.in} is negative").
            to("mock:error-camel-scala")
        }
    }
    //#doc:babel-camel-demo-scala-1

    camelContext.addRoutes(routing)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint1 = camelContext.getEndpoint("mock:database-camel-scala").asInstanceOf[MockEndpoint]
    val mockEndpoint2 = camelContext.getEndpoint("mock:error-camel-scala").asInstanceOf[MockEndpoint]

    mockEndpoint1.expectedBodiesReceived("6")
    mockEndpoint2.expectedBodiesReceived("-1 is negative")

    producer.sendBody("direct:camel-scala", "1,2,3")
    mockEndpoint1.assertIsSatisfied()

    producer.sendBody("direct:camel-scala", "1,2,-4")
    mockEndpoint2.assertIsSatisfied()

  }

}


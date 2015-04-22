/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.{ FoldBodyAggregationStrategy, ReduceBodyAggregationStrategy }
import io.xtech.babel.camel.test.camel
import io.xtech.babel.camel.mock.Mock._
import org.apache.camel.CamelExecutionException
import org.apache.camel.builder.SimpleBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.SimpleRegistry
import org.specs2.mutable.SpecificationWithJUnit

class EnricherSpec extends SpecificationWithJUnit {
  sequential

  "Enricher DSL" should {

    "enrich a message with the enrich keyword and a reference to an aggregationStrategy" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-enricher-1

      val routeDef = new RouteBuilder {
        from("direct:enricherRoute").to("mock:enricher")

        from("direct:input").
          //enriches the input with the enricherRoute messages
          //  using the aggregationStrategy
          enrichRef("direct:enricherRoute", "aggregationStrategy").
          to("mock:output")
      }

      val registry = new SimpleRegistry
      registry.put("aggregationStrategy",
        //the used aggregation strategy is stored in a registry
        new ReduceBodyAggregationStrategy[String]((a, b) => a + b))
      camelContext.setRegistry(registry)
      //#doc:babel-camel-enricher-1

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint({output})
      val enricherMockEndpoint = camelContext.mockEndpoint({enricher})
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "enrich a message with the enrich keyword and an instance of an aggregationStrategy" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-enricher-2
      val aggregationStrategy = new ReduceBodyAggregationStrategy[String]((a, b) => a + b)

      val routeDef = new RouteBuilder {
        from("direct:enricherRoute").to("mock:enricher")

        from("direct:input").
          //enriches the input with the enricherRoute messages
          //  using the aggregationStrategy
          enrich("direct:enricherRoute", aggregationStrategy).
          to("mock:output")
      }

      //#doc:babel-camel-enricher-2

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint({output})
      val enricherMockEndpoint = camelContext.mockEndpoint({enricher})
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "enrich a message with the enrich keyword and a aggregation function" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-enricher-funct

      val routeDef = new RouteBuilder {
        from("direct:enricherRoute").to("mock:enricher")

        from("direct:input").
          requireAs[String].
          //enriches the input with the enricherRoute messages
          //  using the aggregationStrategy
          enrich("direct:enricherRoute", (a: String, b: Any) => s"${a}${b.toString.toInt}").
          to("mock:output")
      }

      //#doc:babel-camel-enricher-func

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("mock:enricher")
      val enricherMockEndpoint = camelContext.mockEndoint("mock:enricher")
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123", "bli123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "bla")
      producer.sendBody("direct:input", "bli")

      mockEndpoint.assertIsSatisfied()
    }

    "do not work with a FoldBodyAggregationStrategy" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      case class Result(string: String) {
        def fold(next: String) = Result(string + next)
      }

      val aggregationStrategy = new FoldBodyAggregationStrategy[String, Result](Result(""), (a, b) => a.fold(b))

      val routeDef = new RouteBuilder {
        from("direct:enricherRoute").to("mock:enricher")

        from("direct:input").
          enrich("direct:enricherRoute", aggregationStrategy).
          to("mock:output")
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val enricherMockEndpoint = camelContext.mockEndoint("mock:enricher")
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "bla") should throwA[CamelExecutionException]

    }

    "enrich a message with the pollEnrich keyword and a reference to an aggregationStrategy" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      // pending

      //#doc:babel-camel-enricher-3

      val routeDef = new RouteBuilder {
        from("direct:input").
          pollEnrichRef("seda:enrichRoute", "aggregationStrategy", 1000).
          to("mock:output")
      }

      val registry = new SimpleRegistry
      registry.put("aggregationStrategy",
        new ReduceBodyAggregationStrategy[String]((a, b) => a + b))
      camelContext.setRegistry(registry)
      //#doc:babel-camel-enricher-3

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint({output})
      val enricherMockEndpoint = camelContext.mockEndpoint({enricher})
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("seda:enrichRoute", "123")
      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "enrich a message with the pollEnrich keyword and an instance of an aggregationStrategy" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //pending

      //#doc:babel-camel-enricher-4

      val aggregationStrategy = new ReduceBodyAggregationStrategy[String]((a, b) => a + b)

      val routeDef = new RouteBuilder {
        from("direct:input").
          pollEnrichAggregation("seda:enrichRoute", aggregationStrategy, 1000).
          to("mock:output")
      }
      //#doc:babel-camel-enricher-4

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint({output})
      val enricherMockEndpoint = camelContext.mockEndpoint({enricher})
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("seda:enrichRoute", "123")
      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

    "enrich a message with the pollEnrich keyword and a aggregationFunction" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //pending

      //#doc:babel-camel-pollenricher-funct
      val routeDef = new RouteBuilder {
        from("direct:input").
          pollEnrich("seda:enrichRoute", (a, b: Any) => s"$a${b.toString}", 1000).
          to("mock:output")
      }
      //#doc:babel-camel-pollenricher-funct

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.start()

      val mockEndpoint = camelContext.mockEndoint("mock:output")
      val enricherMockEndpoint = camelContext.mockEndoint("mock:enricher")
      enricherMockEndpoint.returnReplyBody(new SimpleBuilder("123"))

      mockEndpoint.expectedBodiesReceived("bla123")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("seda:enrichRoute", "123")
      producer.sendBody("direct:input", "bla")

      mockEndpoint.assertIsSatisfied()
    }

  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.ReduceBodyAggregationStrategy
import io.xtech.babel.camel.test.camel
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.{ Exchange, Processor }
import org.specs2.mutable.SpecificationWithJUnit

class SplitSpec extends SpecificationWithJUnit {
  sequential

  "a split that does not throw an exception" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitBody(_.split(",").iterator).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(",")).
          to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    mockBabel1.expectedBodiesReceived("1", "2", "3")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    mockCamel1.expectedBodiesReceived("1", "2", "3")
    mockCamel2.expectedBodiesReceived("1,2,3")

    producer.sendBody("direct:babel", "1,2,3")
    producer.sendBody("direct:camel", "1,2,3")

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()

  }

  "a split that throws an exception" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitBody(_.split(",").iterator).
        processBody(x => if (x == "2") { throw new Exception("exceptected exception") } else { x }).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(",")).
          process(new Processor {
            override def process(exchange: Exchange): Unit = {
              if (exchange.getIn.getBody == "2") {
                throw new Exception("expected camel exception")
              }
            }
          }).to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    mockBabel1.expectedBodiesReceived("1", "3")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    mockCamel1.expectedBodiesReceived("1", "3")
    mockCamel2.expectedBodiesReceived()

    producer.sendBody("direct:babel", "1,2,3") should throwA[Exception]
    producer.sendBody("direct:camel", "1,2,3") should throwA[Exception]

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()

  }

  "a splitFold that throws an exception and stops" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitFoldBody(_.split(",").iterator, stopOnException = true) {
        _.to("mock:babelInner").requireAs[String].
          processBody(x => if (x == "2") { throw new Exception("exceptected exception") } else { x }).
          processBody(x => x.toInt).
          to("mock:after").requireAs[Int]
      }(1)((a, b) => {
        a / b
      }).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val aggregation = new ReduceBodyAggregationStrategy[String]((a, b) => s"$a, $b")

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(","), aggregation).stopOnException().
          to("mock:camelInner").
          process(new Processor {
            override def process(exchange: Exchange): Unit = {
              if (exchange.getIn.getBody == "2") {
                throw new Exception("expected camel exception")
              }
            }
          }).to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    val mockBabelafter = camelContext.mockEndoint("mock:after")
    val mockBabelInner = camelContext.mockEndoint("mock:babelInner")
    mockBabel1.expectedBodiesReceived()
    mockBabelInner.expectedBodiesReceived("1", "2")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    val mockCamelInner = camelContext.mockEndoint("mock:camelInner")
    mockCamel1.expectedBodiesReceived("1")
    mockCamel2.expectedBodiesReceived()
    mockCamelInner.expectedBodiesReceived("1", "2")

    producer.sendBody("direct:babel", "1,2,3") should throwA[Exception]
    producer.sendBody("direct:camel", "1,2,3") should throwA[Exception]

    mockCamel1.assertIsSatisfied()
    mockCamelInner.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()
    mockBabelInner.assertIsSatisfied()

  }

  "a splitReduce that throws an exception and stops" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitReduceBody(_.split(",").iterator, stopOnException = true) {
        _.to("mock:babelInner").requireAs[String].
          processBody(x => if (x == "2") { throw new Exception("exceptected exception") } else { x }).
          processBody(x => x.toInt).
          to("mock:after").requireAs[Int]
      }((a, b) => {
        a / b
      }).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val aggregation = new ReduceBodyAggregationStrategy[String]((a, b) => s"$a, $b")

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(","), aggregation).stopOnException().
          to("mock:camelInner").
          process(new Processor {
            override def process(exchange: Exchange): Unit = {
              if (exchange.getIn.getBody == "2") {
                throw new Exception("expected camel exception")
              }
            }
          }).to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    val mockBabelafter = camelContext.mockEndoint("mock:after")
    val mockBabelInner = camelContext.mockEndoint("mock:babelInner")
    mockBabel1.expectedBodiesReceived()
    mockBabelInner.expectedBodiesReceived("1", "2")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    val mockCamelInner = camelContext.mockEndoint("mock:camelInner")
    mockCamel1.expectedBodiesReceived("1")
    mockCamel2.expectedBodiesReceived()
    mockCamelInner.expectedBodiesReceived("1", "2")

    producer.sendBody("direct:babel", "1,2,3") should throwA[Exception]
    producer.sendBody("direct:camel", "1,2,3") should throwA[Exception]

    mockCamel1.assertIsSatisfied()
    mockCamelInner.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()
    mockBabelInner.assertIsSatisfied()

  }

  "a split that throws an exception and stops" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitBody(_.split(",").iterator, stopOnException = true).
        processBody(x => if (x == "2") { throw new Exception("exceptected exception") } else { x }).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(",")).stopOnException().
          process(new Processor {
            override def process(exchange: Exchange): Unit = {
              if (exchange.getIn.getBody == "2") {
                throw new Exception("expected camel exception")
              }
            }
          }).to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    mockBabel1.expectedBodiesReceived("1")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    mockCamel1.expectedBodiesReceived("1")
    mockCamel2.expectedBodiesReceived()

    producer.sendBody("direct:babel", "1,2,3") should throwA[Exception]
    producer.sendBody("direct:camel", "1,2,3") should throwA[Exception]

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()

  }

  "a split that throws an exception and propagates" in new camel {

    import io.xtech.babel.camel.builder.RouteBuilder

    //#doc:babel-camel-split-reduce
    val routeDef = new RouteBuilder {
      //message bodies are converted to String if required
      from("direct:babel").as[String].splitBody(_.split(",").iterator, propagateException = true).
        processBody(x => if (x == "2") { throw new Exception("exceptected exception") } else { x }).
        to("mock:babel")
    }
    //#doc:babel-camel-split-reduce

    val camelRoute = new CRouteBuilder {
      def configure(): Unit = {
        from("direct:camel").split(body().tokenize(",")).shareUnitOfWork().
          process(new Processor {
            override def process(exchange: Exchange): Unit = {
              if (exchange.getIn.getBody == "2") {
                throw new Exception("expected camel exception")
              }
            }
          }).to("mock:camel1").end().to("mock:camel2")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(camelRoute)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockBabel1 = camelContext.mockEndoint("mock:babel")
    mockBabel1.expectedBodiesReceived("1", "3")

    val mockCamel1 = camelContext.mockEndoint("mock:camel1")
    val mockCamel2 = camelContext.mockEndoint("mock:camel2")
    mockCamel1.expectedBodiesReceived("1", "3")
    mockCamel2.expectedBodiesReceived()

    producer.sendBody("direct:babel", "1,2,3") should throwA[Exception]
    producer.sendBody("direct:camel", "1,2,3") should throwA[Exception]

    mockCamel1.assertIsSatisfied()
    mockCamel2.assertIsSatisfied()
    mockBabel1.assertIsSatisfied()

  }

}

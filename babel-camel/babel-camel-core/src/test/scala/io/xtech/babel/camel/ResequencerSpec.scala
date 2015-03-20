/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.apache.camel.builder.Builder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.config.StreamResequencerConfig
import org.specs2.mutable.SpecificationWithJUnit
import scala.collection.JavaConverters._

class ResequencerSpec extends SpecificationWithJUnit {
  sequential

  "A resequencer" should {

    val list = List(1, 2, 4, 3, 7, 5, 6, 8, 9)
    val resequencedList = List(1, 2, 3, 4, 5, 6, 7, 8, 9)

    "resequence in batch mode" in new camel {
      //#doc:babel-camel-resequence-1

      import io.xtech.babel.camel.builder.RouteBuilder
      import org.apache.camel.model.config.BatchResequencerConfig
      //the resequencing would be done in a batch manner
      val batchConfiguration = new BatchResequencerConfig()

      val routeBuilder = new RouteBuilder {
        //message bodies are converted to Integer if required
        from("direct:input").as[Int].
          //resequencing is based on the body of the message
          resequence(m => m.body.getOrElse(0), batchConfiguration).
          //sends received Integer in a resquenced sequence to the mock endpoint
          to("mock:output")
      }
      //#doc:babel-camel-resequence-1

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(resequencedList.asJava)

      val template = camelContext.createProducerTemplate()

      list.foreach(template.sendBody("direct:input", _))

      mock.assertIsSatisfied()
    }

    "resequence in batch mode with an expression" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder
      import org.apache.camel.model.config.BatchResequencerConfig

      val batchConfiguration = new BatchResequencerConfig()

      val routeBuilder = new RouteBuilder {
        from("direct:input").as[Int].
          resequence(Builder.body(), batchConfiguration).
          to("mock:output")
      }

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(resequencedList.asJava)

      val template = camelContext.createProducerTemplate()

      list.foreach(template.sendBody("direct:input", _))

      mock.assertIsSatisfied()
    }

    "resequence in stream mode" in new camel {
      //#doc:babel-camel-resequence-2

      import io.xtech.babel.camel.builder.RouteBuilder
      import org.apache.camel.model.config.StreamResequencerConfig

      //the resequencing would be done in a streaming manner
      val streamConfiguration = new StreamResequencerConfig()

      val routeBuilder = new RouteBuilder {
        //message bodies are converted to Long if required
        from("direct:input").as[Long].
          //resequencing is based on the body of the message
          resequence(m => m.body.getOrElse(0), streamConfiguration).
          //sends the received Long in a resquenced sequence to the mock endpoint
          to("mock:output")
      }
      //#doc:babel-camel-resequence-2

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(resequencedList.asJava)

      val template = camelContext.createProducerTemplate()

      list.foreach(template.sendBody("direct:input", _))

      mock.assertIsSatisfied()
    }

    "resequence in stream mode with an expression" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val streamConfiguration = new StreamResequencerConfig()

      val routeBuilder = new RouteBuilder {
        from("direct:input").as[Long].
          resequence(Builder.body(), streamConfiguration).
          to("mock:output")
      }

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodiesReceived(resequencedList.asJava)

      val template = camelContext.createProducerTemplate()

      list.foreach(template.sendBody("direct:input", _))

      mock.assertIsSatisfied()
    }
  }

}

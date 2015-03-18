/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.choice

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.test.camel
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.{ Exchange, Predicate, Processor }
import org.specs2.mutable.SpecificationWithJUnit
import scala.collection.JavaConverters._

class SimpleChoiceSpec extends SpecificationWithJUnit {

  "create a route with a choice" in new camel {
    sequential
    //#doc:babel-camel-choice
    val routeDef = new RouteBuilder {
      from("direct:babel").as[String].choice {
        c =>
          c.when(msg => msg.body == Some("1")).
            processBody(body => body + "done").to("mock:output1")
          c.when(msg => msg.body == Some("2")).
            processBody(body => body + "done").to("mock:output2")
          c.when(msg => msg.body == Some("3")).
            processBody(body => body + "done").to("mock:output3")
          c.otherwise.
            processBody(body => body + "done").
            to("mock:output4")
      }
        .to("mock:output5")
      //#doc:babel-camel-choice
    }

    //camel part for validation
    val p1 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "1"
    }
    val p2 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "2"
    }
    val p3 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "3"
    }

    val processor = new Processor {
      def process(p1: Exchange): Unit = {
        p1.getIn().setBody(p1.getIn.getBody + "done")
      }
    }
    val routeBuilder = new CRouteBuilder() {
      def configure(): Unit = {
        from("direct:input").multicast()
          .to("direct:camel")
          .to("direct:babel")
          .end

        from("direct:camel").choice()
          .when(p1).process(processor).to("mock:camelout1")
          .when(p2).process(processor).to("mock:camelout2")
          .when(p3).process(processor).to("mock:camelout3")
          .otherwise().process(processor).to("mock:camelout4")
          .end() //do not use endChoice
          .to("mock:camelout5")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(routeBuilder)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint1 = camelContext.getEndpoint("mock:output1").asInstanceOf[MockEndpoint]
    val mockEndpoint2 = camelContext.getEndpoint("mock:output2").asInstanceOf[MockEndpoint]
    val mockEndpoint3 = camelContext.getEndpoint("mock:output3").asInstanceOf[MockEndpoint]
    val mockEndpoint4 = camelContext.getEndpoint("mock:output4").asInstanceOf[MockEndpoint]
    val mockEndpoint5 = camelContext.getEndpoint("mock:output5").asInstanceOf[MockEndpoint]

    val mockCamelEndpoint1 = camelContext.getEndpoint("mock:camelout1").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint2 = camelContext.getEndpoint("mock:camelout2").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint3 = camelContext.getEndpoint("mock:camelout3").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint4 = camelContext.getEndpoint("mock:camelout4").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint5 = camelContext.getEndpoint("mock:camelout5").asInstanceOf[MockEndpoint]

    mockEndpoint1.expectedBodiesReceived("1done")
    mockCamelEndpoint1.expectedBodiesReceived("1done")
    mockEndpoint2.expectedBodiesReceived("2done")
    mockCamelEndpoint2.expectedBodiesReceived("2done")
    mockEndpoint3.expectedBodiesReceived("3done")
    mockCamelEndpoint3.expectedBodiesReceived("3done")
    mockEndpoint4.expectedBodiesReceived("4done")
    mockCamelEndpoint4.expectedBodiesReceived("4done")
    mockEndpoint5.expectedMessageCount(4)
    mockCamelEndpoint5.expectedMessageCount(4)
    mockEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)
    mockCamelEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)

    producer.sendBody("direct:input", "1")
    producer.sendBody("direct:input", "2")
    producer.sendBody("direct:input", "3")
    producer.sendBody("direct:input", "4")

    mockEndpoint1.assertIsSatisfied()
    mockCamelEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockCamelEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockCamelEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()
    mockCamelEndpoint4.assertIsSatisfied()
    mockEndpoint5.assertIsSatisfied()
    mockCamelEndpoint5.assertIsSatisfied()

  }

  "create a route with a choice using whenBody" in new camel {
    sequential
    //#doc:babel-camel-choice-body
    val routeDef = new RouteBuilder {
      from("direct:babel").as[String].choice {
        c =>
          c.whenBody(body => body == "1").
            processBody(body => body + "done").to("mock:output1")
          c.whenBody(body => body == "2").
            processBody(body => body + "done").to("mock:output2")
          c.whenBody(body => body == "3").
            processBody(body => body + "done").to("mock:output3")
          c.otherwise.
            processBody(body => body + "done").
            to("mock:output4")
      }
        .to("mock:output5")
      //#doc:babel-camel-choice-body
    }

    //camel part for validation
    val p1 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "1"
    }
    val p2 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "2"
    }
    val p3 = new Predicate {
      def matches(p1: Exchange): Boolean = p1.getIn().getBody().toString == "3"
    }

    val processor = new Processor {
      def process(p1: Exchange): Unit = {
        p1.getIn().setBody(p1.getIn.getBody + "done")
      }
    }
    val routeBuilder = new CRouteBuilder() {
      def configure(): Unit = {
        from("direct:input").multicast()
          .to("direct:camel")
          .to("direct:babel")
          .end

        from("direct:camel").choice()
          .when(p1).process(processor).to("mock:camelout1")
          .when(p2).process(processor).to("mock:camelout2")
          .when(p3).process(processor).to("mock:camelout3")
          .otherwise().process(processor).to("mock:camelout4")
          .end() //do not use endChoice
          .to("mock:camelout5")
      }
    }

    routeDef.addRoutesToCamelContext(camelContext)
    camelContext.addRoutes(routeBuilder)
    camelContext.start()

    val producer = camelContext.createProducerTemplate()

    val mockEndpoint1 = camelContext.getEndpoint("mock:output1").asInstanceOf[MockEndpoint]
    val mockEndpoint2 = camelContext.getEndpoint("mock:output2").asInstanceOf[MockEndpoint]
    val mockEndpoint3 = camelContext.getEndpoint("mock:output3").asInstanceOf[MockEndpoint]
    val mockEndpoint4 = camelContext.getEndpoint("mock:output4").asInstanceOf[MockEndpoint]
    val mockEndpoint5 = camelContext.getEndpoint("mock:output5").asInstanceOf[MockEndpoint]

    val mockCamelEndpoint1 = camelContext.getEndpoint("mock:camelout1").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint2 = camelContext.getEndpoint("mock:camelout2").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint3 = camelContext.getEndpoint("mock:camelout3").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint4 = camelContext.getEndpoint("mock:camelout4").asInstanceOf[MockEndpoint]
    val mockCamelEndpoint5 = camelContext.getEndpoint("mock:camelout5").asInstanceOf[MockEndpoint]

    mockEndpoint1.expectedBodiesReceived("1done")
    mockCamelEndpoint1.expectedBodiesReceived("1done")
    mockEndpoint2.expectedBodiesReceived("2done")
    mockCamelEndpoint2.expectedBodiesReceived("2done")
    mockEndpoint3.expectedBodiesReceived("3done")
    mockCamelEndpoint3.expectedBodiesReceived("3done")
    mockEndpoint4.expectedBodiesReceived("4done")
    mockCamelEndpoint4.expectedBodiesReceived("4done")
    mockEndpoint5.expectedMessageCount(4)
    mockCamelEndpoint5.expectedMessageCount(4)
    mockEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)
    mockCamelEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)

    producer.sendBody("direct:input", "1")
    producer.sendBody("direct:input", "2")
    producer.sendBody("direct:input", "3")
    producer.sendBody("direct:input", "4")

    mockEndpoint1.assertIsSatisfied()
    mockCamelEndpoint1.assertIsSatisfied()
    mockEndpoint2.assertIsSatisfied()
    mockCamelEndpoint2.assertIsSatisfied()
    mockEndpoint3.assertIsSatisfied()
    mockCamelEndpoint3.assertIsSatisfied()
    mockEndpoint4.assertIsSatisfied()
    mockCamelEndpoint4.assertIsSatisfied()
    mockEndpoint5.assertIsSatisfied()
    mockCamelEndpoint5.assertIsSatisfied()

  }
}

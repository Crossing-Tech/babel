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

class ComplexChoiceSpec extends SpecificationWithJUnit {
  "a choice" should {
    "accept to have no otherwise branch" in new camel {
      sequential

      val routeDef = new RouteBuilder {
        from("direct:babel").as[String].choice {
          c =>
            c.when(_.body.fold(false)(_ == "1")).processBody(_ + "done").to("mock:output1")
            c.when(_.body.fold(false)(_ == "2")).processBody(_ + "done").to("mock:output2")
            c.when(_.body.fold(false)(_ == "3")).processBody(_ + "done").to("mock:output3")
        }
          .to("mock:output5")
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
      val mockEndpoint5 = camelContext.getEndpoint("mock:output5").asInstanceOf[MockEndpoint]

      val mockCamelEndpoint1 = camelContext.getEndpoint("mock:camelout1").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint2 = camelContext.getEndpoint("mock:camelout2").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint3 = camelContext.getEndpoint("mock:camelout3").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint5 = camelContext.getEndpoint("mock:camelout5").asInstanceOf[MockEndpoint]

      mockEndpoint1.expectedBodiesReceived("1done")
      mockCamelEndpoint1.expectedBodiesReceived("1done")
      mockEndpoint2.expectedBodiesReceived("2done")
      mockCamelEndpoint2.expectedBodiesReceived("2done")
      mockEndpoint3.expectedBodiesReceived("3done")
      mockCamelEndpoint3.expectedBodiesReceived("3done")
      mockEndpoint5.expectedMessageCount(4)
      mockCamelEndpoint5.expectedMessageCount(4)
      mockEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4").asJava)
      mockCamelEndpoint5.expectedBodiesReceived(List("1done", "2done", "3done", "4").asJava)

      producer.sendBody("direct:input", "1")
      producer.sendBody("direct:input", "2")
      producer.sendBody("direct:input", "3")
      producer.sendBody("direct:input", "4")

      mockCamelEndpoint1.assertIsSatisfied()
      mockEndpoint1.assertIsSatisfied()
      mockCamelEndpoint2.assertIsSatisfied()
      mockEndpoint2.assertIsSatisfied()
      mockCamelEndpoint3.assertIsSatisfied()
      mockEndpoint3.assertIsSatisfied()
      mockCamelEndpoint5.assertIsSatisfied()
      mockEndpoint5.assertIsSatisfied()
    }
    "interlock correctly with another one" in new camel {
      sequential

      val routeDef = new RouteBuilder {
        from("direct:babel").as[String].choice {
          c =>
            c.when(_.body.fold(false)(_ == "1")).processBody(_ + "done").to("mock:output1")
            c.when(_.body.fold(false)(_ == "2")).processBody(_ + "done").to("mock:output2")
            c.otherwise.choice {
              d =>
                d.when(_.body.fold(false)(_ == "3")).processBody(_ + "done").to("mock:output3")
                d.otherwise.to("mock:output4")
            }.to("mock:output5")
        }.to("mock:output6")

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
            .otherwise().choice()
            .when(p3).process(processor).to("mock:camelout3")
            .otherwise().to("mock:camelout4")
            .endChoice()
            .to("mock:camelout5")
            .end() //do not use endChoice
            .to("mock:camelout6")
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
      val mockEndpoint6 = camelContext.getEndpoint("mock:output6").asInstanceOf[MockEndpoint]

      val mockCamelEndpoint1 = camelContext.getEndpoint("mock:camelout1").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint2 = camelContext.getEndpoint("mock:camelout2").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint3 = camelContext.getEndpoint("mock:camelout3").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint4 = camelContext.getEndpoint("mock:camelout4").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint5 = camelContext.getEndpoint("mock:camelout5").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint6 = camelContext.getEndpoint("mock:camelout6").asInstanceOf[MockEndpoint]

      mockCamelEndpoint1.expectedBodiesReceived("1done")
      mockEndpoint1.expectedBodiesReceived("1done")
      mockCamelEndpoint2.expectedBodiesReceived("2done")
      mockEndpoint2.expectedBodiesReceived("2done")
      mockCamelEndpoint3.expectedBodiesReceived("3done")
      mockEndpoint3.expectedBodiesReceived("3done")
      mockCamelEndpoint4.expectedBodiesReceived("4")
      mockEndpoint4.expectedBodiesReceived("4")
      mockCamelEndpoint5.expectedMessageCount(1)
      mockEndpoint5.expectedMessageCount(2)
      mockCamelEndpoint5.expectedBodiesReceived(List("4").asJava)
      mockEndpoint5.expectedBodiesReceived(List("3done", "4").asJava)
      mockCamelEndpoint6.expectedMessageCount(4)
      mockEndpoint6.expectedMessageCount(4)
      mockCamelEndpoint6.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)
      mockEndpoint6.expectedBodiesReceived(List("1done", "2done", "3done", "4done").asJava)

      producer.sendBody("direct:input", "1")
      producer.sendBody("direct:input", "2")
      producer.sendBody("direct:input", "3")
      producer.sendBody("direct:input", "4")

      mockCamelEndpoint1.assertIsSatisfied()
      mockEndpoint1.assertIsSatisfied()
      mockCamelEndpoint2.assertIsSatisfied()
      mockEndpoint2.assertIsSatisfied()
      mockCamelEndpoint3.assertIsSatisfied()
      mockEndpoint3.assertIsSatisfied()
      mockCamelEndpoint4.assertIsSatisfied()
      mockEndpoint4.assertIsSatisfied()
      mockCamelEndpoint5.assertIsSatisfied()
      mockEndpoint5.assertIsSatisfied()

    }
    "accept sub routes with filter, processBody and endpoints" in new camel {
      sequential

      val routeDef = new RouteBuilder {
        from("direct:babel").as[String].choice {
          c =>
            c.when(msg => msg.body == Some("2")).filter(_.body.forall(_.contains("2"))).processBody(_ + "done").to("mock:output2")
            c.when(msg => msg.body == Some("3")).filter(_.body.forall(_.contains("2"))).processBody(_ + "done").to("mock:output3")
            c.otherwise.splitBody(_.split(",").iterator).processBody(_ + "done").to("mock:output4")
        }
          .to("mock:output5")
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
            .when(p2).filter(body.contains("2")).process(processor).to("mock:camelout2").endChoice()
            .when(p3).filter(body.contains("2")).process(processor).to("mock:camelout3").endChoice()
            .otherwise().split(body().tokenize(",")).process(processor).to("mock:camelout4").endChoice()
            .end()
            .to("mock:camelout5")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(routeBuilder)
      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint2 = camelContext.getEndpoint("mock:output2").asInstanceOf[MockEndpoint]
      val mockEndpoint3 = camelContext.getEndpoint("mock:output3").asInstanceOf[MockEndpoint]
      val mockEndpoint4 = camelContext.getEndpoint("mock:output4").asInstanceOf[MockEndpoint]
      val mockEndpoint5 = camelContext.getEndpoint("mock:output5").asInstanceOf[MockEndpoint]

      val mockCamelEndpoint2 = camelContext.getEndpoint("mock:camelout2").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint3 = camelContext.getEndpoint("mock:camelout3").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint4 = camelContext.getEndpoint("mock:camelout4").asInstanceOf[MockEndpoint]
      val mockCamelEndpoint5 = camelContext.getEndpoint("mock:camelout5").asInstanceOf[MockEndpoint]

      mockEndpoint2.expectedBodiesReceived("2done")
      mockCamelEndpoint2.expectedBodiesReceived("2done")
      mockEndpoint3.expectedBodiesReceived()
      mockCamelEndpoint3.expectedBodiesReceived()
      mockEndpoint4.expectedBodiesReceived(List(1, 2, 3, 4).map(_ + "done").asJava)
      mockCamelEndpoint4.expectedBodiesReceived(List(1, 2, 3, 4).map(_ + "done").asJava)
      mockCamelEndpoint5.expectedBodiesReceived(List("1,2,3", "2done", "3", "4").asJava)
      mockEndpoint5.expectedBodiesReceived(List("1,2,3", "2done", "3", "4").asJava)

      producer.sendBody("direct:input", "1,2,3")
      producer.sendBody("direct:input", "2")
      producer.sendBody("direct:input", "3")
      producer.sendBody("direct:input", "4")

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
}

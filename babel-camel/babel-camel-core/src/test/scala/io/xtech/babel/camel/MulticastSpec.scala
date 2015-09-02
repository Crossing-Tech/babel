/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.mock._
import io.xtech.babel.camel.model.ReduceBodyAggregationStrategy
import io.xtech.babel.camel.test.camel
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.language.constant.ConstantLanguage
import org.apache.camel.language.simple.SimpleLanguage
import org.apache.camel.{ Exchange, Processor }
import org.apache.camel.builder.{ RouteBuilder => CRouteBuilder }
import org.specs2.mutable.SpecificationWithJUnit

class MulticastSpec extends SpecificationWithJUnit {
  sequential

  "multicast" should {
    " ends correctly" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-multicast

      val routeDef = new RouteBuilder {
        from("direct:input").as[String].
          //received messages are sent to those three mock endpoints
          multicast("mock:output1", "mock:output2", "mock:output3").
          processBody(_ => "tested").
          to("mock:output4").
          processBody(_ + " by babel").
          to("mock:output5")

      }
      //#doc:babel-camel-multicast

      val nativeRoute = new CRouteBuilder() {
        def configure(): Unit = {
          from("direct:inputCamel").
            multicast().to("mock:output1", "mock:output2", "mock:output3").
            //to("log:camel"). WARNING: Adding an endpoint would change the output of the
            log("during:${body}").
            process(new Processor {
              override def process(exchange: Exchange): Unit = {
                exchange.getIn.setBody("tested first")
              }
            }).
            process(new Processor {
              override def process(exchange: Exchange): Unit = {
                exchange.getIn.setBody("tested")
              }
            }).
            end().
            to("mock:output4")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(nativeRoute)

      camelContext.start()

      val mockEndpoint1 = camelContext.mockEndpoint("output1")
      val mockEndpoint2 = camelContext.mockEndpoint("output2")
      val mockEndpoint3 = camelContext.mockEndpoint("output3")
      val mockEndpoint4 = camelContext.mockEndpoint("output4")
      val mockEndpoint5 = camelContext.mockEndpoint("output5")

      val mocks = List(mockEndpoint1, mockEndpoint2, mockEndpoint3, mockEndpoint4)
      val expected = List("test", "test", "test", "tested")
      setMockAssertions(mocks, expected)

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:inputCamel", "test")

      mocks.foreach(_.assertIsSatisfied())
      mocks.foreach(_.reset())

      setMockAssertions(mockEndpoint5 :: mocks, "tested by babel" :: expected)

      producer.sendBody("direct:input", "test")

      (mockEndpoint5 :: mocks).foreach(_.assertIsSatisfied())
    }
    " ends correctly with aggregation" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-multicast-2

      val aggregation = new ReduceBodyAggregationStrategy[String]((x, y) => x)

      val routeDef = new RouteBuilder {
        from("direct:input").as[String].
          //received messages are sent to those three mock endpoints
          multicast("mock:output1", "mock:output2", "mock:output3").withAggregation(aggregation).
          to("mock:output4").
          processBody(_ + " by babel").
          to("mock:output5")

      }
      //#doc:babel-camel-multicast-2

      val nativeRoute = new CRouteBuilder() {
        def configure(): Unit = {
          from("direct:inputCamel").
            multicast(aggregation).to("mock:output1", "mock:output2", "mock:output3").
            //to("log:camel"). WARNING: Adding an endpoint would change the output as this is another branch of the multicast
            log("during:${body}").
            process(new Processor {
              override def process(exchange: Exchange): Unit = {
                exchange.getIn.setBody("tested first")
              }
            }).
            process(new Processor {
              override def process(exchange: Exchange): Unit = {
                exchange.getIn.setBody("tested")
              }
            }).
            end().
            to("mock:output4")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(nativeRoute)

      camelContext.start()

      val mockEndpoint1 = camelContext.mockEndpoint("output1")
      val mockEndpoint2 = camelContext.mockEndpoint("output2")
      val mockEndpoint3 = camelContext.mockEndpoint("output3")
      val mockEndpoint4 = camelContext.mockEndpoint("output4")
      val mockEndpoint5 = camelContext.mockEndpoint("output5")

      mockEndpoint1.returnReplyBody(ConstantLanguage.constant("toto"))
      mockEndpoint2.returnReplyBody(ConstantLanguage.constant("titi"))
      mockEndpoint3.returnReplyBody(ConstantLanguage.constant("tata"))

      val mocks = List(mockEndpoint1, mockEndpoint2, mockEndpoint3, mockEndpoint4)
      val expected = List("test", "test", "test", "toto")
      setMockAssertions(mocks, expected)

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:inputCamel", "test")

      mocks.foreach(_.assertIsSatisfied())
      mocks.foreach(_.reset())

      mockEndpoint1.returnReplyBody(ConstantLanguage.constant("toto"))
      mockEndpoint2.returnReplyBody(ConstantLanguage.constant("titi"))
      mockEndpoint3.returnReplyBody(ConstantLanguage.constant("tata"))

      setMockAssertions(mockEndpoint5 :: mocks, "toto by babel" :: expected)

      producer.sendBody("direct:input", "test")

      (mockEndpoint5 :: mocks).foreach(_.assertIsSatisfied())

    }
  }

  private def setMockAssertions(mocks: List[MockEndpoint], expectations: List[AnyRef]): Unit = {
    mocks.zip(expectations).foreach(me => {
      me._1.expectedBodiesReceived(me._2)
    })
  }
}

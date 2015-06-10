/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.RequireAsSpec.{ A, B }
import io.xtech.babel.camel.test.camel
import org.apache.camel.CamelExecutionException
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

object RequireAsSpec {

  trait A {
    def bla: String
  }

  case class B(bla: String) extends A

}

class RequireAsSpec extends SpecificationWithJUnit {

  private val directConsumer = "direct:input"
  private val mockProducer = "mock:output"

  sequential

  "A RequireAs" should {

    "throw an exception if the type is incorrect" in new camel {

      //#doc:babel-camel-requireAs
      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //Input Message bodies should be of type String
        //  or would throw an Exception
        from(directConsumer).requireAs[String].
          processBody(_ + "4").to(mockProducer)
      }
      //#doc:babel-camel-requireAs
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint(mockProducer).asInstanceOf[MockEndpoint]

      //#doc:babel-camel-requireAs
      val producer = camelContext.createProducerTemplate()
      producer.sendBody(directConsumer, 123) must throwA[CamelExecutionException]
      //#doc:babel-camel-requireAs

    }

    "throw an exception if the type is incorrect" in new camel {

      //#doc:babel-camel-requireAs-exception
      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        //no input Message may satisfies both type constraints,
        //   thus any message sent would throw an Exception.
        from(directConsumer).requireAs[String].requireAs[Int].
          to(mockProducer)
      }
      //#doc:babel-camel-requireAs-exception
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint(mockProducer).asInstanceOf[MockEndpoint]

      //#doc:babel-camel-requireAs-exception
      val producer = camelContext.createProducerTemplate()
      producer.sendBody(directConsumer, "123") must throwA[CamelExecutionException]
      //#doc:babel-camel-requireAs-exception

    }

    "let pass a body with a subclass type" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from(directConsumer).requireAs[A].to(mockProducer)
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint(mockProducer).asInstanceOf[MockEndpoint]

      val b: B = B("bla")
      val a: A = B("bla")

      mockEndpoint.expectedBodiesReceived(a)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody(directConsumer, b)

      mockEndpoint.assertIsSatisfied()
    }

    "supports primitives types" in new camel {
      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from(directConsumer).requireAs[Int].processBody((i: Int) => i).to(mockProducer)
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint(mockProducer).asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived(42: java.lang.Integer)

      val producer = camelContext.createProducerTemplate()
      producer.sendBody(directConsumer, 42)

      mockEndpoint.assertIsSatisfied()
    }
  }

}


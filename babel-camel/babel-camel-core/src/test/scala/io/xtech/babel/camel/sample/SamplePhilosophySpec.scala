/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.sample

import io.xtech.babel.camel.builder.{ RouteBuilder => BabelRouteBuilder }
import io.xtech.babel.camel.test.camel
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit

class SamplePhilosophySpec extends SpecificationWithJUnit {

  "Babel Camel Samples" should {
    " typed  sample" in new camel {

      //#doc:babel-camel-sample-2

      val routeDef = new BabelRouteBuilder {

        //the *requireAs* keyword ensure the from would issue exchange
        //   containing Integer bodies only.
        from("direct:simple").requireAs[java.lang.Integer].
          //the *processBody* knows its input type may only be Integer
          processBody(int => int * 2).
          //the result is transformed to a String and provided to the mock endpoint
          as[String].
          to("mock:double")

        //the *as* keyword ensure the from exchanges are String or transformed to String.
        from("direct:uppercase").as[String].
          //the *processBody* knows its input type may only be String
          processBody(string => string.toLowerCase).
          to("mock:lowercase")

      }

      //#doc:babel-camel-sample-2

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockLower = camelContext.getEndpoint("mock:lowercase").asInstanceOf[MockEndpoint]
      mockLower.expectedBodiesReceived("h2g2")

      val mockDouble = camelContext.getEndpoint("mock:double").asInstanceOf[MockEndpoint]
      mockDouble.expectedBodiesReceived("42")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:simple", 21)
      producer.sendBody("direct:uppercase", "H2G2")

      mockLower.assertIsSatisfied()
      mockDouble.assertIsSatisfied()
    }

    " functionnal  sample" in new camel {

      //#doc:babel-camel-sample-3

      def searchTwitts(input: String): List[String] = {
        //connect to twitter
        //...
        val answer = input.split(" ")
        answer.toList
      }

      val routeDef = new BabelRouteBuilder {

        //the *requireAs* keyword ensure the from would issue exchange
        //   containing String bodies only.
        from("direct:twitts").requireAs[String].
          //the *processBody* knows its input type may only be Integer,
          //   calls directly the searchTwitts function
          processBody(searchTwitts).
          //the searchTwitts outputs a List, using its iterator provides
          //   easy split into several exchanges
          splitBody(list => list.iterator).
          //the mock endpoint receives several String payloads.
          to("mock:twitts")

      }

      //#doc:babel-camel-sample-3

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockTwitts = camelContext.getEndpoint("mock:twitts").asInstanceOf[MockEndpoint]
      mockTwitts.expectedBodiesReceived("h2g2")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:twitts", "h2g2")

      mockTwitts.assertIsSatisfied()
    }

  }
}


/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
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

import io.xtech.babel.fish.model.Message

class SampleTest extends SpecificationWithJUnit {
  sequential

  "Babel Camel Samples" should {
    "first typed and functional sample" in new camel {

      //#doc:babel-camel-sample-1

      val routeDef = new BabelRouteBuilder {

        val splitString = (body: String) => body.split(",").toList

        val isForSwitzerland = (msg: Message[String]) => msg.body.fold(false)(_ == "CH")

        val isForGermany = (msg: Message[String]) => msg.body.fold(false)(_ == "D")

        val filterGermanyErrors = (msg: Message[String]) => {
          !msg.headers.contains("GermanyCurrencyFailure")
        }

        val isForFrance = (msg: Message[String]) => msg.body.fold(false)(_ == "F")

        from("direct:input").as[String]
          .processBody(splitString)
          .splitBody(list => list.iterator)
          .processBody(string => string.toUpperCase)
          .choice {
            c =>
              c.when(isForSwitzerland).to("mock:switzerland")
              c.when(isForFrance).to("mock:france")
              c.when(isForGermany).filter(filterGermanyErrors).to("mock:germany")
          }
          .to("mock:output")
      }

      //#doc:babel-camel-sample-1

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpointF = camelContext.getEndpoint("mock:france").asInstanceOf[MockEndpoint]
      mockEndpointF.expectedBodiesReceived("F")

      val mockEndpointCH = camelContext.getEndpoint("mock:switzerland").asInstanceOf[MockEndpoint]
      mockEndpointCH.expectedBodiesReceived("CH")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "ch,f,d")

      mockEndpointF.assertIsSatisfied()
      mockEndpointCH.assertIsSatisfied()
    }

  }
}


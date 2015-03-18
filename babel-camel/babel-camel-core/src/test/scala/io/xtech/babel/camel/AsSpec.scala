/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.apache.camel.Exchange
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.support.TypeConverterSupport
import org.specs2.mutable.SpecificationWithJUnit
import scala.collection.JavaConverters._

class AsSpec extends SpecificationWithJUnit {
  sequential

  "A As" should {

    "convert the body from Int to String" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-as

      val routeDef = new RouteBuilder {
        //message bodies are converted to String if required
        from("direct:input").as[String]
          //the processBody concatenates received String with "4"
          .processBody(_ + "4")
          //sends the concatenated string to the mock endpoint
          .to("mock:output")
      }
      //#doc:babel-camel-as
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("1234")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", 123)

      mockEndpoint.assertIsSatisfied()
    }

    "convert the body from Int to String with evil type handler" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //if output type is Int, the regular conversion is found and the custom converter is not taken into account
      camelContext.getTypeConverterRegistry.addTypeConverter(classOf[String], classOf[Integer], new MyConverter())

      val routeDef = new RouteBuilder {
        from("direct:input")
          .as[String]
          .processBody(_ + "4")
          .to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived("1234")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", 123)

      mockEndpoint.assertIsNotSatisfied()
    }

    "convert the body using scala dataformat defined by camel-scala" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input")
          .as[List[Int]]
          .to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]

      mockEndpoint.expectedBodiesReceived(List(1, 2, 3))

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", List(1, 2, 3).map(x => Integer.parseInt(x.toString)).asJava)

      mockEndpoint.assertIsSatisfied()
    }
  }

}

class MyConverter extends TypeConverterSupport {
  /**
    * Converts the value to the specified type in the context of an exchange
    * <p/>
    * Used when conversion requires extra information from the current
    * exchange (such as encoding).
    *
    * @param type the requested type
    * @param exchange the current exchange
    * @param value the value to be converted
    * @return the converted value, or <tt>null</tt> if not possible to convert
    * @throws TypeConversionException is thrown if error during type conversion
    */
  def convertTo[T](`type`: Class[T], exchange: Exchange, value: AnyRef): T = {
    "1".asInstanceOf[T]
  }
}


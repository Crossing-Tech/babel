/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.mock._
import io.xtech.babel.camel.model.XPath
import io.xtech.babel.camel.test.camel
import org.apache.camel.builder.xml.XPathBuilder
import org.specs2.mutable.SpecificationWithJUnit

class XMLSpec extends SpecificationWithJUnit {
  sequential

  "xml message" should {

    "support splitting" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val xPathBuilder = new XPathBuilder("//name")

      val routeDef = new RouteBuilder {
        from("direct:input").split(xPathBuilder).to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.mockEndpoint("output")
      mockEndpoint.expectedMessageCount(7)

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>1</name>
          |    <name>2</name>
          |    <name>3</name>
          |    <name>4</name>
          |    <name>5</name>
          |    <name>6</name>
          |    <name>7</name>
          |</person>
        """.stripMargin)
      mockEndpoint.assertIsSatisfied()
    }

    "support filter" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").filter(XPath("//name = 'toto'")).to("mock:output")
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEndpoint = camelContext.mockEndpoint("output")
      mockEndpoint.expectedMessageCount(1)

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>toto</name>
          |</person>
        """.stripMargin)

      mockEndpoint.assertIsSatisfied()

      mockEndpoint.reset()
      mockEndpoint.expectedMessageCount(0)

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>titi</name>
          |</person>
        """.stripMargin)
      mockEndpoint.assertIsSatisfied()
    }

    "support choice" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").as[String].choice {
          c =>
            c.when(XPath("//name = 'toto'")).to("mock:output1")
            c.when(XPath("//name = 'tata'")).to("mock:output2")
            c.otherwise.to("mock:output3")
        }
      }
      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint1 = camelContext.mockEndpoint("output1")
      mockEndpoint1.expectedMessageCount(1)

      val mockEndpoint2 = camelContext.mockEndpoint("output2")
      mockEndpoint2.expectedMessageCount(1)

      val mockEndpoint3 = camelContext.mockEndpoint("output3")
      mockEndpoint3.expectedMessageCount(1)

      val producer = camelContext.createProducerTemplate()

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>toto</name>
          |</person>
        """.stripMargin)

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>tata</name>
          |</person>
        """.stripMargin)

      producer.sendBody("direct:input",
        """<?xml version="1.0" encoding="UTF-8"?>
          |<person>
          |    <name>titi</name>
          |</person>
        """.stripMargin)

      mockEndpoint1.assertIsSatisfied()
      mockEndpoint2.assertIsSatisfied()
      mockEndpoint3.assertIsSatisfied()

    }

  }
}

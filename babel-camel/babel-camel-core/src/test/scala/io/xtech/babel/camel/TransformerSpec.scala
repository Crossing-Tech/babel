/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.SimpleRegistry
import org.specs2.mutable.SpecificationWithJUnit
import io.xtech.babel.camel.mock._

class TestBean {

  def doIt(str: String): String = str + "bla"
}

class TransformerSpec extends SpecificationWithJUnit {
  sequential

  private val mockProducer = "mock:output"

  "A transformation" should {

    "be possible with a bean ref" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-bean-1

      val routeDef = new RouteBuilder {
        from("direct:input").
          //bean keyword is deprecated!
          bean("myBean").to(mockProducer)
      }
      //#doc:babel-camel-bean-1

      val registry = new SimpleRegistry
      registry.put("myBean", new TestBean)

      camelContext.setRegistry(registry)

      routeDef.addRoutesToCamelContext(camelContext)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

    "be possible with a bean ref and a method name" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-bean-2
      val routeDef = new RouteBuilder {
        from("direct:input").
          //the received message are provided to the "doIt" method
          //   of the class with bean id "myBean"
          //bean keyword is deprecated!
          bean("myBean", "doIt").
          //the bean keyword destroys the type of the next keyword
          to(mockProducer)
      }
      //#doc:babel-camel-bean-2
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      registry.put("myBean", new TestBean)

      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

    "be possible with a bean object" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-bean-3
      val routeDef = new RouteBuilder {
        from("direct:input").
          //the received message are provided to
          //   the TestBean class method which corresponds
          //bean keyword is deprecated!
          bean(new TestBean).
          to(mockProducer)
      }
      //#doc:babel-camel-bean-3
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

    "be possible with a bean object and a method name" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").bean(new TestBean, "doIt").to(mockProducer)
      }
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

    "be possible with a bean class" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-bean-4
      val routeDef = new RouteBuilder {
        from("direct:input").
          //the received message are provided to
          //   the TestBean class method which corresponds
          //bean keyword is deprecated!
          bean(classOf[TestBean]).
          to(mockProducer)
      }
      //#doc:babel-camel-bean-4
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

    "be possible with a bean class with a method name" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeDef = new RouteBuilder {
        from("direct:input").bean(classOf[TestBean], "doIt").to(mockProducer)
      }
      routeDef.addRoutesToCamelContext(camelContext)

      val registry = new SimpleRegistry
      camelContext.setRegistry(registry)

      camelContext.start()

      val mockEndpoint = camelContext.mockEndpoint("output")

      mockEndpoint.expectedBodiesReceived("testbla")

      val producer = camelContext.createProducerTemplate()
      producer.sendBody("direct:input", "test")

      mockEndpoint.assertIsSatisfied()
    }

  }

}

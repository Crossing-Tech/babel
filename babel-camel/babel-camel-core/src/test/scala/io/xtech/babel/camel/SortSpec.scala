/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import java.util.{ List => JList }
import org.apache.camel.builder.Builder
import org.apache.camel.component.mock.MockEndpoint
import org.specs2.mutable.SpecificationWithJUnit
import scala.collection.JavaConverters._
import scala.collection.mutable

class SortSpec extends SpecificationWithJUnit {
  sequential

  "A sort" should {

    "sort string element in a message with the default comparator" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-sort-1

      val routeBuilder = new RouteBuilder {
        //the input string is "4,3,1,2"
        from("direct:input").as[String].
          //the message body is split and then its output is sorted
          sort(msg => msg.body.getOrElse("").split(",")).
          //the output is List("1", "2", "3", "4")
          to("mock:output")
      }
      //#doc:babel-camel-sort-1

      camelContext.addRoutes(routeBuilder)
      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodyReceived().constant(mutable.ListBuffer("1", "2", "3", "4").asJava)

      val template = camelContext.createProducerTemplate()
      template.sendBody("direct:input", "4,3,1,2")

      mock.assertIsSatisfied()
    }

    "sort int element in a message with a comparator" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeBuilder = new RouteBuilder {
        from("direct:input").as[String].
          sort(msg => msg.body.getOrElse("").split(",").map(_.toInt), EvenOddOrdering).to("mock:output")
      }

      camelContext.addRoutes(routeBuilder)
      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodyReceived().constant(mutable.ListBuffer(2, 4, 1, 3).asJava)

      val template = camelContext.createProducerTemplate()
      template.sendBody("direct:input", "4,3,1,2")

      mock.assertIsSatisfied()
    }

    "sort int element in a message using an expression" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      val routeBuilder = new RouteBuilder {
        from("direct:input").as[JList[Int]].
          sort(Builder.body()).to("mock:output")
      }

      camelContext.addRoutes(routeBuilder)
      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodyReceived().constant(mutable.ListBuffer(1, 2, 3, 4).asJava)

      val template = camelContext.createProducerTemplate()
      template.sendBody("direct:input", mutable.ListBuffer(4, 3, 1, 2).asJava)

      mock.assertIsSatisfied()
    }

    "sort int element in a message using an expression with a comparator" in new camel {

      //#doc:babel-camel-sort-2-1

      import io.xtech.babel.camel.builder.RouteBuilder
      import java.util.{ List => JList }
      import org.apache.camel.builder.Builder

      val routeBuilder = new RouteBuilder {
        //The sort keyword expects Java list type
        from("direct:input").as[JList[Int]].
          //the exchanges are sorted based on their body
          sort(Builder.body(), EvenOddOrdering).
          to("mock:output")
      }
      //#doc:babel-camel-sort-2-1

      camelContext.addRoutes(routeBuilder)
      camelContext.start()

      val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

      mock.expectedBodyReceived().constant(mutable.ListBuffer(2, 4, 1, 3).asJava)

      val template = camelContext.createProducerTemplate()
      template.sendBody("direct:input", mutable.ListBuffer(4, 3, 1, 2).asJava)

      mock.assertIsSatisfied()
    }

  }
  //#doc:babel-camel-sort-2-2

  import scala.math.Ordering.IntOrdering

  //The EvenOddOrdering would order the Integer depending on
  //   if they are even or odd
  //1,2,3,4 becomes 2,4,1,3
  object EvenOddOrdering extends IntOrdering {
    override def compare(a: Int, b: Int): Int = (a, b) match {
      case (a, b) if (a % 2) == (b % 2) =>
        Ordering.Int.compare(a, b)
      case (a, b) if a % 2 == 0 =>
        -1
      case (a, b) =>
        1
    }
  }

  //#doc:babel-camel-sort-2-2

}

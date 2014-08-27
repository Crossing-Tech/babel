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

package io.xtech.babel.camel.builder

import io.xtech.babel.camel.test.springContext
import org.specs2.mutable.SpecificationWithJUnit
import org.apache.camel.CamelContext
import org.apache.camel.component.mock.MockEndpoint

class RouteBuilderTest extends SpecificationWithJUnit {
  sequential

  "Babel Camel route builder" should {
    "be defined by the packageScan" in new springContext("classpath:/META-INF/spring/context.xml") {
      val context: CamelContext = spring.getBean(classOf[CamelContext])
      val mock = context.getEndpoint("mock:babel-rb").asInstanceOf[MockEndpoint]
      mock.expectedBodiesReceived("Hello RB 1", "Hello RB 2")
      val producer = context.createProducerTemplate()
      producer.sendBody("direct:babel-rb-1", "Hello RB 1")
      producer.sendBody("direct:babel-rb-2", "Hello RB 2")
      mock.assertIsSatisfied should not(throwA[Exception])
    }

    "accept injection base on setter" in new springContext("classpath:/META-INF/spring/context-setter-injection.xml") {
      val context: CamelContext = spring.getBean(classOf[CamelContext])

      val mock = context.getEndpoint("mock:babel-rb-setter").asInstanceOf[MockEndpoint]
      mock.expectedBodiesReceived("Hello RB 1bla")
      val producer = context.createProducerTemplate()
      producer.sendBody("direct:babel-rb-setter", "Hello RB 1")
      mock.assertIsSatisfied should not(throwA[Exception])
    } //.pendingUntilFixed("refactor Babel Camel RouteBuilder to support setter injection")

    "accept injection base on constructor" in new springContext("classpath:/META-INF/spring/context-constructor-injection.xml") {
      val context: CamelContext = spring.getBean(classOf[CamelContext])

      val mock = context.getEndpoint("mock:babel-rb-setter").asInstanceOf[MockEndpoint]
      mock.expectedBodiesReceived("Hello RB 1bla")
      val producer = context.createProducerTemplate()
      producer.sendBody("direct:babel-rb-setter", "Hello RB 1")
      mock.assertIsSatisfied should not(throwA[Exception])
    } //.pendingUntilFixed("message about the issue")
  }

}

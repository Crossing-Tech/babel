/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.test

import org.apache.camel.Exchange
import org.apache.camel.impl.{ DefaultCamelContext, DefaultExchange }
import org.specs2.mutable.After
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
  * Declare a CamelContext in the Context of a Specs2 example.
  */
class camel extends After {

  lazy implicit val camelContext = new DefaultCamelContext()

  def createExchange(): Exchange = new DefaultExchange(camelContext)

  def after: Unit = {
    camelContext.shutdown()
  }
}

/**
  * Declare a Spring Application Context in the Context of a Specs2 example.
  * @param configLocation the location of a spring xml file.
  */
class springContext(configLocation: String) extends After {

  lazy val spring = new ClassPathXmlApplicationContext(configLocation)

  def after: Unit = {
    spring.close()
  }
}

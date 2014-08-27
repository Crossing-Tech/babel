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

package io.xtech.babel.camel.test

import org.apache.camel.impl.{ DefaultCamelContext, DefaultExchange }
import org.specs2.mutable.After
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
  * Declare a CamelContext in the Context of a Specs2 example.
  */
class camel extends After {

  lazy implicit val camelContext = new DefaultCamelContext()

  def createExchange() = new DefaultExchange(camelContext)

  def after {
    camelContext.shutdown()
  }
}

/**
  * Declare a Spring Application Context in the Context of a Specs2 example.
  * @param configLocation the location of a spring xml file.
  */
class springContext(configLocation: String) extends After {

  lazy val spring = new ClassPathXmlApplicationContext(configLocation)

  def after {
    spring.close()
  }
}

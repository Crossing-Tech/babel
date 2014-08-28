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

import org.apache.camel.CamelContext

/**
  * The SpringRouteBuilder is helpful to define Babel Camel routes which are dealing with Spring Beans.
  * As those beans may not have been initialized, the Babel Camel routes should be define in the body of the configure method.
  */
trait SpringRouteBuilder extends RouteBuilder {

  /**
    * The configure method should contain the Babel Camel route definition.
    * It ensures the used Spring Beans are initialized before initializing the route definitions.
    * Used by the Camel RouteBuilder to load defined Babel Camel routes in the calling Camel Context.
    */

  def configure(): Unit

  override def addRoutesToCamelContext(context: CamelContext) = {
    configure()
    super.addRoutesToCamelContext(context)
  }

}

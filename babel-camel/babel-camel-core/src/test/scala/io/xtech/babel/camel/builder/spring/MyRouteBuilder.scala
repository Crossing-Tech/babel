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

//#doc:babel-camel-spring
package io.xtech.babel.camel.builder.spring

import io.xtech.babel.camel.builder.RouteBuilder

class MyRouteBuilder extends RouteBuilder {
  from("direct:babel-rb-1").to("mock:babel-rb")
  from("direct:babel-rb-2").to("mock:babel-rb")
}
//#doc:babel-camel-spring

/*
 *
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

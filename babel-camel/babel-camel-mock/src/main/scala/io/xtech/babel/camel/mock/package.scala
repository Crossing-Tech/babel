/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import org.apache.camel.CamelContext
import org.apache.camel.component.mock.MockEndpoint

package object mock {

  implicit class CamelContextWithMock(val self: CamelContext) extends AnyVal {

    def getMockEndpoint(uri: String): MockEndpoint = mockEndpoint(uri)

    def mockEndpoint(uri: String): MockEndpoint = self.getEndpoint(s"mock:$uri").asInstanceOf[MockEndpoint]
  }

}

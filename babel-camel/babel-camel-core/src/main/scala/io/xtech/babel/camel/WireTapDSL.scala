/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.{ CamelSink, WireTapDefinition }
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the wiretap keyword to the BaseDSL.
  */
private[camel] class WireTapDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The wiretap keyword. Defines an endpoint where a copy of exchanges are forwarded, leaving the normal flow of the route.
    * @param uri of the endpoint which receives a copy of each exchange
    * @return the possibility to add other steps to the current DSL
    */
  def wiretap(uri: String): BaseDSL[I] = WireTapDefinition[I](CamelSink[I](uri))

}


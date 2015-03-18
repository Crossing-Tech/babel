/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.ChannelDefinition
import io.xtech.babel.fish.{ BaseDSL, BaseDSL2FromDSL, FromDSL }
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the routeId keyword to the FromDSL (start of the route).
  */
private[camel] class SubRouteDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends BaseDSL2FromDSL[I] {

  /**
    * the sub keyword.
    * @param routeId a given id for a new sub route.
    * @return the possibility to add other steps to the current DSL.
    */
  def sub(routeId: String): FromDSL[I] = {
    //todo use a macro to ensure id is correct string
    require(Option(routeId).exists(_.trim.length > 0), "routeId can neither be null nor empty")

    ChannelDefinition(routeId)

  }
}


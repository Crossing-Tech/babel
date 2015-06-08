/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.ErrorHandlingRouteDefinition
import io.xtech.babel.fish.{ NoDSL, BaseDSL, BaseDSL2FromDSL, FromDSL }

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the routeId keyword to the FromDSL (start of the route).
  */
private[camel] class SubRouteDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) {

  /**
    * the sub keyword.
    * @param errorHandlingChannel a given id for a new sub route.
    * @return the possibility to add other steps to the current DSL.
    */
  def handlingRoute(errorHandlingChannel: String): NoDSL = {
    //todo use a macro to ensure id is correct string
    require(Option(errorHandlingChannel).exists(_.trim.length > 0), "errorHandling Route can neither be null nor empty")

    val definition = ErrorHandlingRouteDefinition(errorHandlingChannel)
    baseDsl.step.next = Some(definition)
    new NoDSL {}

  }
}


/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.{ IdDefinition, RouteIdDefinition }
import io.xtech.babel.fish.{ BaseDSL, BaseDSL2FromDSL, FromDSL }

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the routeId keyword to the FromDSL (start of the route).
  */
private[camel] class RouteIdDSL[I: ClassTag](protected val baseDsl: FromDSL[I]) extends BaseDSL2FromDSL[I] {

  /**
    * the routeId keyword.
    * @param id a given id for a route.
    * @return the possibility to add other steps to the current DSL
    */
  def routeId(id: String): FromDSL[I] = {
    require(Option(id).exists(_.trim.length > 0), "routeId can neither be null nor empty")

    RouteIdDefinition(id)
  }
}

private[camel] class IdDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends BaseDSL2FromDSL[I] {
  /**
    * the id keyword.
    * @param id a given id for the previous eip.
    * @return the possibility to add other steps to the current DSL
    */
  def id(id: String): BaseDSL[I] = {
    require(Option(id).exists(_.trim.length > 0), "id can neither be null nor empty")

    IdDefinition(id)

  }
}


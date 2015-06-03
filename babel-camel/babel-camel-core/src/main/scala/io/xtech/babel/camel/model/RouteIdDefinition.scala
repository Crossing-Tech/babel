/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.StepDefinition

/**
  * Declaration of an id for a route.
  * @param routeId the route id.
  */
case class RouteIdDefinition(val routeId: String) extends StepDefinition

case class FromIdDefinition(val Id: String) extends StepDefinition
case class IdDefinition(val Id: String) extends StepDefinition

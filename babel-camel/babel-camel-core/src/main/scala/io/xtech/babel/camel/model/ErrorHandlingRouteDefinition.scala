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
  * Declaration of a link to an Error handling Route configuration.
  * @param endpoint which connects to the error handling route.
  */
case class ErrorHandlingRouteDefinition(endpoint: String) extends StepDefinition

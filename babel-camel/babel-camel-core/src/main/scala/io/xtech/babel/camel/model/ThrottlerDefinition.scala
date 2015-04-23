/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ Expression, StepDefinition }

/**
  * Declaration of a throttler definition
  * @param perSecond maximal number of message allowed per second
  */
case class ThrottlerDefinitionLong(perSecond: Long) extends StepDefinition

/**
  * Declaration of a throttler definition
  * @param perSecond expression defining maximal number of message allowed per second
  */
case class ThrottlerDefinitionExpression[I](perSecond: Expression[I, Long]) extends StepDefinition

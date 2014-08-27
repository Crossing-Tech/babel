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
  * Defines a RecipientList EIP (Publish and Subscribe)
  */
case class RecipientListDefinition[I, E](expression: Expression[I, E]) extends StepDefinition

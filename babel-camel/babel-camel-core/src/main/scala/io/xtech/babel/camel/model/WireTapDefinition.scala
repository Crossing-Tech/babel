/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.model.StepDefinition

/**
  * Defines a sink where each message is just copied.
  */
case class WireTapDefinition[T](sink: CamelSink[T]) extends StepDefinition


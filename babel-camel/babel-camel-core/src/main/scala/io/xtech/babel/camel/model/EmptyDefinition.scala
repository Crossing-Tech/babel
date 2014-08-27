/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ FromDefinition, Source }

/**
  * The EmptySource is used in the EmptyDefinition to specify there is no source to this definition.
  * @see io.xtech.babel.camel.model.EmptySource
  */
object EmptySource extends Source[Any]

/**
  * The EmptyDefinition takes place of the FromDefinition as beginning of a Route Definition. Such RouteDefinition are
  * defining the error management for the whole RouteBuilder.
  */
class EmptyDefinition extends FromDefinition(classOf[Any], EmptySource)

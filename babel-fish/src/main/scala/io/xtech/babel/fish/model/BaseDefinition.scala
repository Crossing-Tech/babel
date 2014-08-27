/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

import scala.collection.immutable

/*
 * The base classes for a route definition
 */

/**
  * A RouteDefinition contains the definition of the DSL after it has been build.
  * @param from the beginning of the route.
  */
case class RouteDefinition(from: FromDefinition)

/*
 * The definitions of some basic blocks (EIP, etc ...)
 */

/**
  * The definition of a sink in the route.
  * @param sink the sink itself.
  */
case class EndpointDefinition[I, O](sink: Sink[I, O], requestReply: Option[Boolean] = None) extends StepDefinition

/**
  * The definition of a multicast. A multicast routes the same message to multiple endpoints (sink).
  * @param sinks the endpoints.
  * @tparam I the body type of the input message.
  */
case class MulticastDefinition[I](sinks: immutable.Seq[Sink[I, _]]) extends StepDefinition

/**
  * The definition of a body message conversion.
  * @param outClass the new type of the body message.
  */
case class BodyConvertorDefinition[I, O](inClass: Class[I], outClass: Class[O]) extends StepDefinition

/**
  * The definition of a body type validation.
  * @param outClass the required type.
  */
case class BodyTypeValidationDefinition[I, O](inClass: Class[I], outClass: Class[O]) extends StepDefinition


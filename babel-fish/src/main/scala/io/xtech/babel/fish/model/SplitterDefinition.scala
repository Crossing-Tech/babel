/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

/**
  * The definition of message splitter. It split a message in pieces.
  * @param expression how the messages are splitted.
  * @tparam I the message body type.
  * @tparam O the result type after the splitting.
  */
case class SplitterDefinition[I, O](expression: Expression[I, O], stopOnException: Boolean, propagateException: Boolean) extends StepDefinition with SplitConfiguration

case class SplitReduceDefinition[I, O, G](expression: Expression[I, O], reduce: (G, G) => G, stopOnException: Boolean, propagateException: Boolean) extends StepDefinition with SplitConfiguration {
  val internalRouteDefinition = new StepDefinition() {}

}

case class SplitFoldDefinition[I, O, G, H](expression: Expression[I, O], seed: H, fold: (H, G) => H, stopOnException: Boolean, propagateException: Boolean) extends StepDefinition with SplitConfiguration {
  val internalRouteDefinition = new StepDefinition() {}
}

trait SplitConfiguration {
  def stopOnException: Boolean

  def propagateException: Boolean
}

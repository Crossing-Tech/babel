/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

/**
  * The definition of some message transformation.
  * @param expression how the message is transformed.
  * @tparam I the type of the body.
  * @tparam O the type of the body after transformation.
  */
case class TransformerDefinition[I, O](expression: Expression[I, O]) extends StepDefinition

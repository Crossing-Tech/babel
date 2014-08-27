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
  * The definition of a transaction.
  * @param ref a reference.
  */
case class TransactionDefinition(ref: Option[String] = None) extends StepDefinition

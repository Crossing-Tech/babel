/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

/**
  * The definition of filtering message in a route.
  * @param predicate the condition used for the filtering.
  * @tparam I the body type.
  */
case class FilterDefinition[I](predicate: Predicate[I]) extends StepDefinition


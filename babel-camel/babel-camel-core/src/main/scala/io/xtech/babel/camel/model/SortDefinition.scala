/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ Expression, StepDefinition }
import java.util.Comparator

/**
  * The definition of the sort keyword in the DSL
  * @param expression what element of the message is sorted.
  * @param comparator  how are the element sorted.
  * @tparam I input type.
  * @tparam O output type.
  */
case class SortDefinition[I, O, C](expression: Expression[I, O], comparator: Option[Comparator[C]]) extends StepDefinition

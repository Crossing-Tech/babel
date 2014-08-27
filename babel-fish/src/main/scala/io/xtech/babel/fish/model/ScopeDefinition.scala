/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

import scala.collection.immutable

/**
  * defines an EIP which may have several outputs
  */
class ScopeDefinition[D <: StepDefinition] extends StepDefinition {

  /**
    * @return a list of subroutes
    */
  var scopedSteps: immutable.IndexedSeq[D] = immutable.IndexedSeq.empty

}

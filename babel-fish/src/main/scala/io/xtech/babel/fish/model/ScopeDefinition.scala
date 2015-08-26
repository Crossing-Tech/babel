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
protected[babel] class ScopeDefinition[D <: StepDefinition] extends StepDefinition {

  /**
    * @return a list of subroutes
    */
  protected[babel] var scopedSteps: immutable.IndexedSeq[D] = immutable.IndexedSeq.empty

}

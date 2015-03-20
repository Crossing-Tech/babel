/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.model._

import scala.language.implicitConversions
import scala.reflect.ClassTag


/**
 * sub DSL for the MultiDSL. Let define branches to cast to.
 * @param multi represents the multicast to which we are adding branches.
 * @tparam I the input type of this keyword.
 */
class MulticastFunctionalDSL[I: ClassTag](multi: MulticastFunctionalDefinition[I]) extends BaseDSL[I](multi) {

  /**
   * @return the possibility to add other steps to the current DSL
   */
  def route(id: String): BaseDSL[I] = {

    val whenDef = new MultiDefinition[I](id)

    multi.scopedSteps = multi.scopedSteps :+ whenDef

    new BaseDSL[I](whenDef)
  }
}

/**
 * DSL adding the multi keyword, a functional version of the multicast.
 */

class MultiDSL[I: ClassTag](step: StepDefinition) extends BaseDSL[I](step) {

  def multi(cast: (MulticastFunctionalDSL[I] => Unit)): BaseDSL[I] = {
    val multicast = new MulticastFunctionalDefinition[I]
    step.next = Some(multicast)

    val multiDSL = new MulticastFunctionalDSL[I](multicast)
    cast(multiDSL)

    new BaseDSL(multicast)
  }
}


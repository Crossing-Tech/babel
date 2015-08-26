/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model.StepDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

trait DSLEnricher

/**
  * Template DSL whose keywords are typing a BaseDSL
  */
trait DSL2BaseDSL[I] extends DSLEnricher {

  protected def baseDsl: BaseDSL[I]

  protected implicit def addAStepToTheDSL[II: ClassTag](definition: StepDefinition): BaseDSL[II] = {
    baseDsl.step.next = Some(definition)
    new BaseDSL[II](definition)
  }

}

/**
  * Template DSL whose keywords are typing a FromDSL
  */
trait BaseDSL2FromDSL[I] extends DSLEnricher {

  protected def baseDsl: BaseDSL[I]

  protected implicit def addAStepToTheDSL[II: ClassTag](definition: StepDefinition): FromDSL[II] = {
    baseDsl.step.next = Some(definition)
    new FromDSL[II](definition)
  }

}

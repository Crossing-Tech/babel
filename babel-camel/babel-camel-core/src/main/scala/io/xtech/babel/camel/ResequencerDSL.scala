/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.ResequencerDefinition
import io.xtech.babel.fish.model.{ Expression, Message }
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL, MessageExpression }
import org.apache.camel.model.config.ResequencerConfig

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * DSL adding the resequence keyword.
  */
private[camel] class ResequencerDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The resequencer keyword in the dsl.
    * @param function on what part of the message the comparaison are done.
    * @param resequencerConfig an implementation of ResequencerConfig
    * @tparam E the type of the message, header, or a piece of the message
    * @return the possibility to add other steps to the current DSL
    */
  def resequence[E](function: Message[I] => E, resequencerConfig: ResequencerConfig): BaseDSL[I] = {

    ResequencerDefinition(MessageExpression(function), resequencerConfig)

  }

  /**
    * The resequencer keyword in the dsl.
    * @param expression on what part of the message the comparaison are done.
    * @param resequencerConfig an implementation of ResequencerConfig
    * @tparam E the type of the message, header, or a piece of the message
    * @return the possibility to add other steps to the current DSL
    */
  def resequence[E](expression: Expression[I, E], resequencerConfig: ResequencerConfig): BaseDSL[I] = {

    ResequencerDefinition(expression, resequencerConfig)

  }
}


/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ StepDefinition, Expression }

import org.apache.camel.model.config.ResequencerConfig

/**
  * The definition of a resequencer eip.
  * @param expression on what part of the message the comparaison are done.
  * @param resequencer the configuration of the resquencer.
  * @tparam I the type of the message body.
  * @tparam E the type of the message, header, or a piece of the message.
  */
case class ResequencerDefinition[I, E](expression: Expression[I, E], resequencer: ResequencerConfig) extends StepDefinition

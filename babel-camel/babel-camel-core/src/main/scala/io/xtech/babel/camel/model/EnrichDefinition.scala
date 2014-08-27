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

import io.xtech.babel.fish.model.{ Sink, StepDefinition }

/**
  * The definition a enrich keyword in the DSL
  */
case class EnrichDefinition[I, O](val sink: Sink[I, O], val aggregationStrategyRef: String) extends StepDefinition

/**
  * The definition a pollEnrich keyword in the DSL
  */
case class PollEnrichDefinition[I, O](val sink: Sink[I, O], val aggregationStrategyRef: String, val timeout: Long = -1) extends StepDefinition

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

package io.xtech.babel.fish.model

/**
  * The base trait for all declaration of an aggregator.
  * Each DSL (Babel Camel, Babel Fish) need to create their own aggregation declaration with this trait.
  * @tparam I the message body type before the aggregation.
  * @tparam O the message body type after the aggregation.
  */
trait AggregationConfiguration[I, O]

/**
  * The definition a message filtering in a route.
  * @param configuration something the configure the aggregation.
  * @tparam I the body type of the input message.
  * @tparam O the body type after the aggregation.
  */
case class AggregationDefinition[I, O](configuration: AggregationConfiguration[I, O]) extends StepDefinition


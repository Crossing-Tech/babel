/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ Sink, StepDefinition }
import org.apache.camel.processor.aggregate.AggregationStrategy

/**
  * The definition of the enrichRef keyword in the DSL
  */
case class EnrichRefDefinition[I, O](sink: Sink[I, O], aggregationStrategyRef: String) extends StepDefinition

/**
  * The definition of the enrich keyword in the DSL
  */
case class EnrichDefinition[I, O](sink: Sink[I, O], aggregationStrategy: AggregationStrategy) extends StepDefinition

/**
 * The definition of the enrich keyword in the DSL
 */
case class EnrichFunctionalDefinition[I, O, T](sink: Sink[I, O], aggregationFunction: (I, O) => T) extends StepDefinition

/**
  * The definition of the pollEnrichRef keyword in the DSL
  */
case class PollEnrichRefDefinition[I, O](sink: Sink[I, O], aggregationStrategyRef: String, timeout: Long = -1) extends StepDefinition

/**
  * The definition of the pollEnrich keyword in the DSL
  */
case class PollEnrichDefinition[I, O](sink: Sink[I, O], aggregationStrategy: AggregationStrategy, timeout: Long = -1) extends StepDefinition

/**
 * The definition of the pollEnrich keyword in the DSL
 */
case class PollEnrichFunctionalDefinition[I, O, T](sink: Sink[I, O], aggregationFunction: (I, O) => T, timeout: Long = -1) extends StepDefinition

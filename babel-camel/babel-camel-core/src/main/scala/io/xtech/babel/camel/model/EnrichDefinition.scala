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
case class EnrichDefinition[I, O](sink: Sink[I, O], aggregationStrategy: Either[String, AggregationStrategy]) extends StepDefinition

/**
  * The definition of the pollEnrichRef keyword in the DSL
  */
case class PollEnrichDefinition[I, O](sink: Sink[I, O], aggregationStrategy: Either[String, AggregationStrategy], timeout: Long = -1) extends StepDefinition


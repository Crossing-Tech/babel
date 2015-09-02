/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.StepDefinition
import org.apache.camel.processor.aggregate.AggregationStrategy

/**
 * The definition of how a multicast aggregates its messages.
 * @param aggregation how multicast is aggregating its messages.
 * @tparam I the body type of the input message.
 */
case class MulticastAggregationDefinition[I](aggregation: AggregationStrategy) extends StepDefinition
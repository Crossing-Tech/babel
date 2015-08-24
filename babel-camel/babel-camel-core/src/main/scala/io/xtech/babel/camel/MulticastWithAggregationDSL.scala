/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.MulticastAggregationDefinition
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import org.apache.camel.processor.aggregate.AggregationStrategy

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the wiretap keyword to the BaseDSL.
  */
private[camel] class MulticastWithAggregationDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * Multicast configuration. Defines how a multicast aggregates its messages.
    * @param aggregate the AggregationStrategy used to aggregates messages output by the multicast branches.
    * @return the possibility to add other steps to the current DSL
    */
  def withAggregation(aggregate: AggregationStrategy): BaseDSL[I] = MulticastAggregationDefinition[I](aggregate)

}


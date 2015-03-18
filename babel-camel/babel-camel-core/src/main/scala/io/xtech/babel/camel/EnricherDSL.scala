/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.camel.model.{ EnrichDefinition, EnrichRefDefinition, PollEnrichDefinition, PollEnrichRefDefinition }
import io.xtech.babel.fish.model.Sink
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import org.apache.camel.processor.aggregate.AggregationStrategy
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * DSL adding the enrichment keywords.
  * @param baseDsl
  * @param evidence$1
  * @tparam I
  */
private[camel] class EnricherDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * Enrich a message with the data coming from an endpoint using request-reply pattern.
    * @param sink the endpoint.
    * @param aggregationStrategyRef a reference of a bean from a Registry (ex: Spring Application Context).
    *                               how the original message and the message coming from the endpoint are aggregated.
    * @param convert converts the endpoint to a Sink.
    * @tparam O the output type of the enrichment.
    * @tparam S the native type of the Sink.
    * @return the possibility to add other steps to the current DSL.
    */
  def enrichRef[O: ClassTag, S](sink: S, aggregationStrategyRef: String)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    EnrichRefDefinition[I, O](sink, aggregationStrategyRef)
  }

  /**
    * Enrich a message with the data coming from an endpoint using request-reply pattern.
    * @param sink the endpoint.
    * @param aggregationStrategy an instance of an AggregationStrategy that know
    *                               how the original message and the message coming from the endpoint are aggregated.
    * @param convert converts the endpoint to a Sink.
    * @tparam O the output type of the enrichment.
    * @tparam S the native type of the Sink.
    * @return the possibility to add other steps to the current DSL.
    */
  def enrich[O: ClassTag, S](sink: S, aggregationStrategy: AggregationStrategy)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    EnrichDefinition[I, O](sink, aggregationStrategy)
  }

  /**
    * Enrich a message with data coming from an enpoint. The pollEnrich keyword is polling the endpoint.
    * @param sink the endpoint.
    * @param aggregationStrategyRef a reference of a bean from a Registry (ex: Spring Application Context).
    *                               how the original message and the message coming from the endpoint are aggregated.
    * @param timeout the timeout when polling the endpoint in milliseconds.
    *                Possible values : -1 (block until there is a message, 0 don't wait and return immediately, otherwise wait a specific period of time.
    * @param convert converts the endpoint to a Sink.
    * @tparam O the output type of the enrichment.
    * @tparam S the native type of the Sink.
    * @return the possibility to add other steps to the current DSL.
    */
  def pollEnrichRef[O: ClassTag, S](sink: S, aggregationStrategyRef: String, timeout: Int = -1)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    PollEnrichRefDefinition[I, O](sink, aggregationStrategyRef, timeout)

  }

  /**
    * Enrich a message with data coming from an enpoint. The pollEnrich keyword is polling the endpoint.
    * @param sink the endpoint.
    * @param aggregationStrategy an instance of an AggregationStrategy that know
    *                               how the original message and the message coming from the endpoint are aggregated.
    * @param timeout the timeout when polling the endpoint in milliseconds.
    *                Possible values : -1 (block until there is a message, 0 don't wait and return immediately, otherwise wait a specific period of time.
    * @param convert converts the endpoint to a Sink.
    * @tparam O the output type of the enrichment.
    * @tparam S the native type of the Sink.
    * @return the possibility to add other steps to the current DSL.
    */
  def pollEnrich[O: ClassTag, S](sink: S, aggregationStrategy: AggregationStrategy, timeout: Int = -1)(implicit convert: S => Sink[I, O]): BaseDSL[O] = {

    PollEnrichDefinition[I, O](sink, aggregationStrategy, timeout)

  }
}


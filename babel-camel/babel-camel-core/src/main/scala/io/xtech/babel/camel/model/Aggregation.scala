/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ AggregationConfiguration, Expression, Message }
import org.apache.camel.processor.aggregate.AggregationStrategy
import scala.collection.immutable

/**
  * facility object containing the required definitions to build an Aggregation EIP,
  * used in user routes.
  */
object Aggregation {

  /**
    * Base trait for all the possible ways to complete an aggregation.
    */
  sealed trait CompletionStrategy

  /**
    * Completes an aggregation by number of messages.
    * @param size the number of messages
    */
  case class CompletionSize(size: Int) extends CompletionStrategy

  /**
    * Completes an aggregation after a lapse of time.
    * @param time the lapse of time in milliseconds
    */
  case class CompletionInterval(time: Long) extends CompletionStrategy

  /**
    * Completes an aggregation after an inactivity period.
    * @param time the period in milliseconds
    */
  case class CompletionTimeout(time: Long) extends CompletionStrategy

  /**
    * Completes an aggregation when the context is stopped.
    */
  case object ForceCompletionOnStop extends CompletionStrategy

  /**
    * Completes an aggregation when all messages from a batch are received.
    */
  case object CompletionFromBatchConsumer extends CompletionStrategy

  /**
    * Uses the camel way to define an aggregation
    * @param correlationExpression how the message are grouped.
    * @param aggregationStrategy how the aggregation is done with an AggregationStrategy.
    * @param completionStrategies when the aggregation are complete.
    * @tparam I the message body type before the aggregation.
    * @tparam G the type of the correlation key.
    */
  case class CamelAggregation[I, G](correlationExpression: Expression[I, G],
                                    aggregationStrategy: AggregationStrategy,
                                    completionStrategies: immutable.Seq[CompletionStrategy]) extends AggregationConfiguration[I, Any]

  /**
    * Uses the camel way to define an aggregation
    * @param correlationExpression how the message are grouped.
    * @param aggregationRef how the aggregation is done using a reference in the Camel Registry.
    * @param completionStrategies when the aggregation are complete.
    * @tparam I the message body type before the aggregation.
    * @tparam G the type of the correlation key.
    */
  case class CamelReferenceAggregation[I, G](correlationExpression: Expression[I, G],
                                             aggregationRef: String,
                                             completionStrategies: immutable.Seq[CompletionStrategy]) extends AggregationConfiguration[I, Any]

  /**
    * Cumulates a single result using message bodies.
    * @param reduce the function that cumulates.
    * @param groupBy how the message are grouped.
    * @param completionStrategies when the aggregation are complete.
    * @tparam I the message body type before the aggregation.
    * @tparam G The type of the key use for grouping.
    */
  case class ReduceBody[I, G](reduce: (I, I) => I, groupBy: (Message[I] => G), completionStrategies: immutable.Seq[CompletionStrategy]) extends AggregationConfiguration[I, I]

  /**
    * Cumulate a single result using a start value and the message bodies.
    * @param seed the start value.
    * @param fold the function that cumulates.
    * @param groupBy how the message are grouped.
    * @param completionStrategies when the aggregation are complete.
    * @tparam I the message body type before the aggregation.
    * @tparam O the message body type after the aggregation and the type of the start value.
    * @tparam G The type of the key use for grouping.
    */
  case class FoldBody[I, O, G](seed: O, fold: (O, I) => O, groupBy: (Message[I] => G), completionStrategies: immutable.Seq[CompletionStrategy]) extends AggregationConfiguration[I, O]

}

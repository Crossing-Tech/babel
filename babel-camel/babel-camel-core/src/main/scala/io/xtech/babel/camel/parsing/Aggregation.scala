/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model.Aggregation._
import io.xtech.babel.camel.model.{ CamelMessageExpression, Expressions, FoldBodyAggregationStrategy, ReduceBodyAggregationStrategy }
import io.xtech.babel.fish.model.AggregationDefinition
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.{ AggregateDefinition, ProcessorDefinition }
import scala.collection.immutable

/**
  * Defines the parsing of aggregation definitions.
  */
private[babel] trait Aggregation extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def competion(aggregateDefinition: AggregateDefinition, completionStrategies: immutable.Seq[CompletionStrategy]) {
    for (completion <- completionStrategies) {
      completion match {
        case CompletionInterval(time) => {
          aggregateDefinition.setCompletionInterval(time)
        }
        case CompletionSize(size) => {
          aggregateDefinition.setCompletionSize(size)
        }
        case CompletionTimeout(time) => {
          aggregateDefinition.setCompletionTimeout(time)
        }
        case ForceCompletionOnStop => {
          aggregateDefinition.forceCompletionOnStop()
        }
        case CompletionFromBatchConsumer => {
          aggregateDefinition.completionFromBatchConsumer()
        }
      }
    }
  }

  // parsing of an aggregation definition
  private[this] def parse: Process = {

    // parse a native camel aggregation
    case StepInformation(AggregationDefinition(CamelAggregation(correlationExpression, aggregationStrategy, completionStrategies)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val nextProcDef = camelProcessorDefinition.aggregate(Expressions.toCamelExpression(correlationExpression), aggregationStrategy)

      competion(nextProcDef, completionStrategies)

      nextProcDef
    }

    case StepInformation(AggregationDefinition(CamelReferenceAggregation(correlationExpression, ref, completionStrategies)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val nextProcDef = camelProcessorDefinition.aggregate.expression(Expressions.toCamelExpression(correlationExpression)).aggregationStrategyRef(ref)

      competion(nextProcDef, completionStrategies)

      nextProcDef
    }

    // parse a reduce definition
    case StepInformation(AggregationDefinition(ReduceBody(reduce, groupBy, completionStrategies)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      val aggregationStrategy = new ReduceBodyAggregationStrategy(reduce)

      val nextProcDef = camelProcessorDefinition.aggregate(CamelMessageExpression(groupBy), aggregationStrategy)

      competion(nextProcDef, completionStrategies)

      nextProcDef
    }

    // parse a fold definition
    case StepInformation(AggregationDefinition(FoldBody(seed, fold, groupBy, completionStrategies)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      val aggregationStrategy = new FoldBodyAggregationStrategy(seed, fold)

      val nextProcDef = camelProcessorDefinition.aggregate(CamelMessageExpression(groupBy), aggregationStrategy)

      competion(nextProcDef, completionStrategies)

      nextProcDef
    }
  }
}


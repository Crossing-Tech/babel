/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.EnricherDSL
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.{ EnrichDefinition => CamelEnrichDefinition, PollEnrichDefinition => CamelPollEnrichDefinition, ProcessorDefinition }
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Defines the enrich and pollEnrich keyword in the DSL.
  */
private[babel] trait Enricher extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def enrichDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new EnricherDSL(baseDsl)

  /**
    * Parsing of the enricher feature
    */
  private[this] def parse: Process = {

    // parsing of the enrichRef keyword
    case StepInformation(EnrichRefDefinition(CamelSink(resourceUri), aggregationRef), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelEnrichDefinition = new CamelEnrichDefinition
      camelEnrichDefinition.setResourceUri(resourceUri)
      camelEnrichDefinition.setAggregationStrategyRef(aggregationRef)

      camelProcessorDefinition.addOutput(camelEnrichDefinition)

      camelProcessorDefinition
    }

    // parsing of the enrich keyword
    case StepInformation(EnrichDefinition(CamelSink(resourceUri), aggregationStrategy), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelEnrichDefinition = new CamelEnrichDefinition
      camelEnrichDefinition.setResourceUri(resourceUri)
      camelEnrichDefinition.setAggregationStrategy(aggregationStrategy)

      camelProcessorDefinition.addOutput(camelEnrichDefinition)

      camelProcessorDefinition
    }

    // parsing of the pollEnrich keyword
    case StepInformation(PollEnrichRefDefinition(CamelSink(resourceUri), aggregationRef, timeout), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelPollEnrichDefinition = new CamelPollEnrichDefinition
      camelPollEnrichDefinition.setResourceUri(resourceUri)
      camelPollEnrichDefinition.setAggregationStrategyRef(aggregationRef)
      camelPollEnrichDefinition.setTimeout(timeout)

      camelProcessorDefinition.addOutput(camelPollEnrichDefinition)

      camelProcessorDefinition
    }

    // parsing of the pollEnrichRef keyword
    case StepInformation(PollEnrichDefinition(CamelSink(resourceUri), aggregationStrategy, timeout), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelPollEnrichDefinition = new CamelPollEnrichDefinition
      camelPollEnrichDefinition.setResourceUri(resourceUri)
      camelPollEnrichDefinition.setAggregationStrategy(aggregationStrategy)
      camelPollEnrichDefinition.setTimeout(timeout)

      camelProcessorDefinition.addOutput(camelPollEnrichDefinition)

      camelProcessorDefinition
    }
  }

}

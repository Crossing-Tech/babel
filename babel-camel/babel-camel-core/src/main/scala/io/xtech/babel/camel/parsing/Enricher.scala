/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.EnricherDSL
import io.xtech.babel.camel.model.{ PollEnrichDefinition, CamelSink, EnrichDefinition }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.{ EnrichDefinition => CamelEnrichDefinition, PollEnrichDefinition => CamelPollEnrichDefinition, ProcessorDefinition }
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Defines the enrich and pollEnrich keyword in the DSL.
  */
private[babel] trait Enricher extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def enrichDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new EnricherDSL(baseDsl)

  /**
    * Parsing of the enricher feature
    */
  private def parse: Process = {

    // parsing of the enrich keyword
    case StepInformation(EnrichDefinition(CamelSink(resourceUri), aggregationRef), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelEnrichDefinition = new CamelEnrichDefinition
      camelEnrichDefinition.setResourceUri(resourceUri)
      camelEnrichDefinition.setAggregationStrategyRef(aggregationRef)

      camelProcessorDefinition.addOutput(camelEnrichDefinition)

      camelProcessorDefinition
    }

    // parsing of the pollenrich keyword
    case StepInformation(PollEnrichDefinition(CamelSink(resourceUri), aggregationRef, timeout), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val camelPollEnrichDefinition = new CamelPollEnrichDefinition
      camelPollEnrichDefinition.setResourceUri(resourceUri)
      camelPollEnrichDefinition.setAggregationStrategyRef(aggregationRef)
      camelPollEnrichDefinition.setTimeout(timeout)

      camelProcessorDefinition.addOutput(camelPollEnrichDefinition)

      camelProcessorDefinition
    }

  }

}

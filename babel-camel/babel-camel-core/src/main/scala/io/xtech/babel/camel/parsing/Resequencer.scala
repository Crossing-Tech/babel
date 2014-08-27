/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.ResequencerDSL
import io.xtech.babel.camel.model.{ ResequencerDefinition, Expressions }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.config.{ StreamResequencerConfig, BatchResequencerConfig }

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The parser of resequencer definition.
  */
private[babel] trait Resequencer extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def resequencerDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new ResequencerDSL(baseDsl)

  private def parse: Process = {

    case StepInformation(ResequencerDefinition(expression, batch: BatchResequencerConfig), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.resequence(Expressions.toCamelExpression(expression)).batch(batch)

    }
    case StepInformation(ResequencerDefinition(expression, stream: StreamResequencerConfig), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.resequence(Expressions.toCamelExpression(expression)).stream(stream)

    }
  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model.{ Expressions, ResequencerDefinition }
import io.xtech.babel.camel.{ CamelDSL, ResequencerDSL }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.config.{ BatchResequencerConfig, StreamResequencerConfig }

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The parser of resequencer definition.
  */
private[babel] trait Resequencer extends CamelParsing {
  self: CamelDSL =>

  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def parse: Process = {

    case StepInformation(step @ ResequencerDefinition(expression, batch: BatchResequencerConfig), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.resequence(Expressions.toCamelExpression(expression)).batch(batch).withId(step)

    }
    case StepInformation(step @ ResequencerDefinition(expression, stream: StreamResequencerConfig), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.resequence(Expressions.toCamelExpression(expression)).stream(stream).withId(step)

    }
  }

  protected implicit def resequencerDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): ResequencerDSL[I] = new ResequencerDSL(baseDsl)
}

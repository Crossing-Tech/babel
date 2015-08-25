/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model._
import io.xtech.babel.camel.{ CamelDSL, LogDSL }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The log parser.
  */
private[babel] trait Log extends CamelParsing {
  self: CamelDSL =>

  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def parse: Process = {

    case StepInformation(step @ LogDefinition(LogMessage(message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(message).withId(step)
    }

    case StepInformation(step @ LogDefinition(LogLoggingLevelMessage(level, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, message).withId(step)
    }

    case StepInformation(step @ LogDefinition(LogLoggingLevelLogNameMessage(level, name, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, name, message).withId(step)
    }

    case StepInformation(step @ LogDefinition(LogLoggingLevelLogNameMarkerMessage(level, name, marker, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, name, marker, message).withId(step)
    }
  }

  protected implicit def logDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): LogDSL[I] = new LogDSL(baseDsl)
}

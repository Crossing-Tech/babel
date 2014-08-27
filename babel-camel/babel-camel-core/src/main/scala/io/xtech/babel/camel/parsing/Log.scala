/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.LogDSL
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition
import scala.reflect.ClassTag
import scala.language.implicitConversions

/**
  * The log parser.
  */
private[babel] trait Log extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def logDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new LogDSL(baseDsl)

  private def parse: Process = {

    case StepInformation(LogDefinition(LogMessage(message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(message)
    }

    case StepInformation(LogDefinition(LogLoggingLevelMessage(level, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, message)
    }

    case StepInformation(LogDefinition(LogLoggingLevelLogNameMessage(level, name, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, name, message)
    }

    case StepInformation(LogDefinition(LogLoggingLevelLogNameMarkerMessage(level, name, marker, message)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.log(level, name, marker, message)
    }
  }
}

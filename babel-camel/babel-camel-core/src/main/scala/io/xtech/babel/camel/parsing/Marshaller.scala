/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.MarshallerDSL
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition
import scala.reflect.ClassTag
import scala.language.implicitConversions

/**
  * The `marshall` / `unmarshall` parser
  */
private[babel] trait Marshaller extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def marshallerDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new MarshallerDSL(baseDsl)

  /**
    * Parsing of the marshalling feature
    */
  private def parse: Process = {
    case StepInformation(MarshallerDefinition(MarshallerInstance(dataFormat)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.marshal(dataFormat)

    }
    case StepInformation(MarshallerDefinition(MarshallerReference(ref)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.marshal(ref)

    }
    case StepInformation(MarshallerDefinition(UnmarshallerInstance(dataFormat)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.unmarshal(dataFormat)

    }
    case StepInformation(MarshallerDefinition(UnmarshallerReference(ref)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.unmarshal(ref)

    }
  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.{ CamelDSL, MarshallerDSL }
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The `marshall` / `unmarshall` parser
  */
private[babel] trait Marshaller extends CamelParsing { self: CamelDSL =>

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  protected implicit def marshallerDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new MarshallerDSL(baseDsl)

  /**
    * Parsing of the marshalling feature
    */
  private[this] def parse: Process = {
    case StepInformation(step @ MarshallerDefinition(MarshallerInstance(dataFormat)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.marshal(dataFormat).withId(step)

    }
    case StepInformation(step @ MarshallerDefinition(MarshallerReference(ref)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.marshal(ref).withId(step)

    }
    case StepInformation(step @ MarshallerDefinition(UnmarshallerInstance(dataFormat)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.unmarshal(dataFormat).withId(step)

    }
    case StepInformation(step @ MarshallerDefinition(UnmarshallerReference(ref)), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.unmarshal(ref).withId(step)

    }
  }
}

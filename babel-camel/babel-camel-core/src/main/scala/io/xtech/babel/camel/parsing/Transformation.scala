/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.{ CamelDSL, TransformationDSL }
import io.xtech.babel.camel.model.{ BeanClassExpression, BeanNameExpression, BeanObjectExpression }
import io.xtech.babel.fish.model.{ Message, TransformerDefinition }
import io.xtech.babel.fish.parsing.StepInformation
import io.xtech.babel.fish.{ BaseDSL, BodyExpression, MessageTransformationExpression }
import org.apache.camel.model.ProcessorDefinition

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Parser for the transformation definitions
  */
private[babel] trait Transformation extends CamelParsing { self: CamelDSL =>

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  protected implicit def transformationDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): TransformationDSL[I] = new TransformationDSL(baseDsl)

  private[this] def bodyFunctionToProcess[I, O](function: (I => O)): org.apache.camel.Processor = new CamelBodyProcessor(function)

  private[this] def messageFunctionToProcess[I, O](function: (Message[I] => Message[O])): org.apache.camel.Processor = new CamelMessageProcessor(function)

  /**
    * Parses the "processBody" statement.
    * @see CamelDSL.StepImplementation
    */
  private[this] def parse: Process = {

    case StepInformation(step @ TransformerDefinition(BodyExpression(function)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(bodyFunctionToProcess(function))
      camelProcessorDefinition.withId(step)
    }

    case StepInformation(step @ TransformerDefinition(MessageTransformationExpression(function)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(messageFunctionToProcess(function))
      camelProcessorDefinition.withId(step)
    }

    case StepInformation(step @ TransformerDefinition(BeanNameExpression(beanRef, method)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.beanRef(beanRef))(methodName => camelProcessorDefinition.beanRef(beanRef, methodName)).withId(step)

    }

    // use an instance of a bean as a transformer
    case StepInformation(step @ TransformerDefinition(BeanObjectExpression(obj, method)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.bean(obj))(m => camelProcessorDefinition.bean(obj, m)).withId(step)
    }

    // create a transformer from a class description
    case StepInformation(step @ TransformerDefinition(BeanClassExpression(clazz, method)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.bean(clazz))(m => camelProcessorDefinition.bean(clazz, m)).withId(step)
    }
  }
}

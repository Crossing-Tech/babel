/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.TransformationDSL
import io.xtech.babel.camel.model.{ BeanClassExpression, BeanNameExpression, BeanObjectExpression }
import io.xtech.babel.fish.{ MessageTransformationExpression, BodyExpression, BaseDSL }
import io.xtech.babel.fish.model.{ TransformerDefinition, Message }
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition

import scala.reflect.ClassTag
import scala.language.implicitConversions

/**
  * Parser for the transformation definitions
  */
private[babel] trait Transformation extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def transformationDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new TransformationDSL(baseDsl)

  def toProcessor[I, O](function: (I => O)) = new CamelBodyProcessor(function)

  def bodyFunctionToProcess[I, O](function: (I => O)): org.apache.camel.Processor = new CamelBodyProcessor(function)

  def messageFunctionToProcess[I, O](function: (Message[I] => Message[O])): org.apache.camel.Processor = new CamelMessageProcessor(function)

  /**
    * Parses the "processBody" statement.
    * @see CamelDSL.StepImplementation
    */
  private def parse: Process = {

    case StepInformation(TransformerDefinition(BodyExpression(function), processorId), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(bodyFunctionToProcess(function))
      processorId.foreach(camelProcessorDefinition.id(_))
      camelProcessorDefinition
    }

    case StepInformation(TransformerDefinition(MessageTransformationExpression(function), processorId), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(messageFunctionToProcess(function))
      processorId.foreach(camelProcessorDefinition.id(_))
      camelProcessorDefinition
    }

    case StepInformation(TransformerDefinition(BeanNameExpression(beanRef, method), _), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.beanRef(beanRef))(methodName => camelProcessorDefinition.beanRef(beanRef, methodName))

    }

    // use an instance of a bean as a transformer
    case StepInformation(TransformerDefinition(BeanObjectExpression(obj, method), _), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.bean(obj))(m => camelProcessorDefinition.bean(obj, m))
    }

    // create a transformer from a class description
    case StepInformation(TransformerDefinition(BeanClassExpression(clazz, method), _), camelProcessorDefinition: ProcessorDefinition[_]) => {

      method.fold(camelProcessorDefinition.bean(clazz))(m => camelProcessorDefinition.bean(clazz, m))
    }
  }
}

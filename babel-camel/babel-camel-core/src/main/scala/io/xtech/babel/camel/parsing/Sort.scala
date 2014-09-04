/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.SortDSL
import io.xtech.babel.camel.model.{ SortDefinition, Expressions }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The sort parser.
  */
private[babel] trait Sort extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def sortDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new SortDSL(baseDsl)

  private def parse: Process = {

    case StepInformation(SortDefinition(expression, Some(comparator)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.sort(Expressions.toJavaListCamelExpression(expression), comparator)

    }

    case StepInformation(SortDefinition(expression, None), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.sort(Expressions.toJavaListCamelExpression(expression))

    }
  }
}

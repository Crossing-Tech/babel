/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.{ CamelDSL, SortDSL }
import io.xtech.babel.camel.model.{ Expressions, SortDefinition }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The sort parser.
  */
private[babel] trait Sort extends CamelParsing { self: CamelDSL =>

  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit protected def sortDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): SortDSL[I] = new SortDSL(baseDsl)

  private[this] def parse: Process = {

    case StepInformation(step @ SortDefinition(expression, Some(comparator)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.sort(Expressions.toJavaListCamelExpression(expression), comparator).withId(step)

    }

    case StepInformation(step @ SortDefinition(expression, None), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.sort(Expressions.toJavaListCamelExpression(expression)).withId(step)

    }
  }
}

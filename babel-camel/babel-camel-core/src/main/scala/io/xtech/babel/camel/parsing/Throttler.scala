/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.{ CamelDSL, ThrottlerDSL }
import io.xtech.babel.camel.model.{ Expressions, ThrottlerDefinitionExpression, ThrottlerDefinitionLong }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.reflect.ClassTag

/**
  * Defines the parsing of the throttle keyword.
  */
trait Throttler extends CamelParsing { self: CamelDSL =>

  abstract override def steps = super.steps :+ parse

  private val parse: Process = {
    case StepInformation(step @ ThrottlerDefinitionLong(perSecond), camelProcessor: ProcessorDefinition[_]) =>
      camelProcessor.throttle(perSecond).withId(step)

    case StepInformation(step @ ThrottlerDefinitionExpression(perSecond), camelProcessor: ProcessorDefinition[_]) =>
      camelProcessor.throttle(Expressions.toCamelExpression(perSecond)).withId(step)

  }

  implicit def throttlerDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new ThrottlerDSL(baseDsl)
}

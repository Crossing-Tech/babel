/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.ValidationDSL
import io.xtech.babel.camel.model.{ Predicates, ValidationDefinition }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

private[babel] trait Validation extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def validationDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new ValidationDSL(baseDsl)

  // parsing of an validation definition
  private[this] def parse: Process = {

    case StepInformation(ValidationDefinition(expression), camelProcessorDefinition: ProcessorDefinition[_]) => {
      camelProcessorDefinition.validate(Predicates.toCamelPredicate(expression))
    }
  }
}

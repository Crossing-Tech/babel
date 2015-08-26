/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model.{ Expressions, RecipientListDefinition }
import io.xtech.babel.camel.{ CamelDSL, RecipientListDSL }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.model.StepDefinition
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The recipientList parser.
  */
private[babel] trait RecipientList extends CamelParsing {
  self: CamelDSL =>

  // insert the extension in the base fish
  protected implicit def recipientListDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new RecipientListDSL(baseDsl)

  // add the recipientList parser to the other parsers
  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def parse: Process = {
    case StepInformation(definition @ RecipientListDefinition(expression), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.recipientList(Expressions.toCamelExpression(expression)).withId(definition)

    }
  }
}

class RecipientlistDSL[I: ClassTag](step: StepDefinition) extends BaseDSL[I](step)

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

import io.xtech.babel.camel.RecipientListDSL
import io.xtech.babel.camel.model.{ Expressions, RecipientListDefinition }
import io.xtech.babel.fish.parsing.StepInformation
import io.xtech.babel.fish.BaseDSL

import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The recipientList parser.
  */
private[babel] trait RecipientList extends CamelParsing {

  // insert the extension in the base fish
  implicit def recipientListDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new RecipientListDSL(baseDsl)

  // add the recipientList parser to the other parsers
  abstract override def steps = super.steps :+ parse

  private def parse: Process = {
    case StepInformation(definition @ RecipientListDefinition(expression), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.recipientList(Expressions.toCamelExpression(expression))

    }
  }
}

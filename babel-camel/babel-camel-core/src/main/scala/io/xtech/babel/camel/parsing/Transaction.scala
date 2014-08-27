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

import io.xtech.babel.camel.TransactionDSL
import io.xtech.babel.camel.model.TransactionDefinition
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation

import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Defines a transaction in the CamelDSL.
  */
private[babel] trait Transaction extends CamelParsing {

  abstract override def steps = super.steps :+ parse

  implicit def transactedDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new TransactionDSL(baseDsl)

  // parsing of an transaction definition
  private def parse: Process = {

    case StepInformation(TransactionDefinition(ref), camelProcessorDefinition: ProcessorDefinition[_]) => {

      ref.fold(camelProcessorDefinition.transacted())(ref => camelProcessorDefinition.transacted(ref))

    }

  }
}

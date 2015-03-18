/*
 *
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
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Defines a transaction in the CamelDSL.
  */
private[babel] trait Transaction extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def transactedDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): TransactionDSL[I] = new TransactionDSL(baseDsl)

  // parsing of an transaction definition
  private[this] def parse: Process = {

    case StepInformation(TransactionDefinition(ref), camelProcessorDefinition: ProcessorDefinition[_]) => {

      ref.fold(camelProcessorDefinition.transacted())(ref => camelProcessorDefinition.transacted(ref))

    }

  }
}

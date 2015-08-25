/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model.TransactionDefinition
import io.xtech.babel.camel.{ CamelDSL, TransactionDSL }
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
  self: CamelDSL =>

  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  // parsing of an transaction definition
  private[this] def parse: Process = {

    case StepInformation(step @ TransactionDefinition(ref), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val nextCamelProcessor = ref.fold(camelProcessorDefinition.transacted())(ref => camelProcessorDefinition.transacted(ref))
      nextCamelProcessor.withId(step)

    }

  }

  protected implicit def transactedDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): TransactionDSL[I] = new TransactionDSL(baseDsl)
}

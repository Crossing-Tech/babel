/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.WireTapDSL
import io.xtech.babel.camel.model.WireTapDefinition
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The wiretap parser.
  */
private[babel] trait WireTap extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def wiretapDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new WireTapDSL(baseDsl)

  private[this] def parse: Process = {

    case StepInformation(WireTapDefinition(sink), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.wireTap(sink.uri)

    }

  }
}

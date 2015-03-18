/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.RouteIdDSL
import io.xtech.babel.camel.model.RouteIdDefinition
import io.xtech.babel.fish.FromDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The routeId parser.
  */
private[babel] trait RouteId extends CamelParsing {
  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def routeIdDSLExtension[I: ClassTag](baseDsl: FromDSL[I]): RouteIdDSL[I] = new RouteIdDSL(baseDsl)

  private[this] def parse: Process = {

    case StepInformation(RouteIdDefinition(routeId), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.routeId(routeId)

    }
  }
}

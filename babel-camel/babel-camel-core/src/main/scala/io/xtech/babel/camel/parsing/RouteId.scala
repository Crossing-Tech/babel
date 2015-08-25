/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model._
import io.xtech.babel.camel.{ CamelDSL, IdDSL, RouteIdDSL }
import io.xtech.babel.fish.model.FromDefinition
import io.xtech.babel.fish.parsing.StepInformation
import io.xtech.babel.fish.{ BaseDSL, FromDSL }
import org.apache.camel.model.{ ProcessorDefinition, RouteDefinition }

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.Try

/**
  * The routeId parser.
  */
private[babel] trait RouteId extends CamelParsing {
  self: CamelDSL =>
  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def parse: Process = {

    case s @ StepInformation(RouteIdDefinition(routeId), camelProcessorDefinition: ProcessorDefinition[_]) => {

      //renames the from id
      s.previousStepHelper match {
        case from: RouteDefinition =>
          Try {
            namingStrategy.newRoute()
            namingStrategy.routeId = Some(routeId)
            namingStrategy.name(FromDefinition(classOf[Any], from.getInputs.get(0).getUri())).foreach(from.id(_))
          }
        case other: Any =>
          namingStrategy.routeId = Some(routeId)
      }
      camelProcessorDefinition.routeId(routeId)

    }

    case StepInformation(IdDefinition(id), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.id(id)

    }

  }

  implicit protected def routeIdDSLExtension[I: ClassTag](baseDsl: FromDSL[I]): RouteIdDSL[I] = new RouteIdDSL(baseDsl)

  implicit protected def idDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): IdDSL[I] = new IdDSL(baseDsl)
}

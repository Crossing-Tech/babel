/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.{CamelDSL, MultiDSL}
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.model.MulticastFunctionalDefinition
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.ProcessorDefinition

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
 * The `multi` parser
 */
private[babel] trait MulticastFunctional extends CamelParsing {
  self: CamelDSL =>

  abstract override def steps = super.steps :+ parse

  implicit def multicastDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]) = new MultiDSL[I](baseDsl.step)

  /**
   * Parsing of the functional mutlicast
   */
  private def parse: Process = {
    case s@StepInformation(multicast: MulticastFunctionalDefinition[_], camelProcessorDefinition: ProcessorDefinition[_]) => {

      val uris = multicast.scopedSteps.map(x => s"direct:${x.branchId}")
      val javaUris = uris.toArray

      multicast.scopedSteps.foreach(multi => {
        val rest = s.buildHelper.from(s"direct:${multi.branchId}").routeId(multi.branchId)
        multi.next.foreach(n => process(n, rest)(s.buildHelper))
      })

      camelProcessorDefinition.multicast().to(javaUris: _*)

    }
  }
}

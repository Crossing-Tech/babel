/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.{ ThrottlerDefinitionExpression, ThrottlerDefinitionLong }
import io.xtech.babel.fish.model.{ Expression, Message }
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL, MessageExpression }

import scala.reflect.ClassTag

/**
  * DSL adding the throttle keyword
  */
class ThrottlerDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The throttle keyword. Defines the maximal rate that may be use in order to avoid overloading of the rest of the route
    * @param perSecond number of allowed messages in 1 second
    * @return the possibility to add other steps to the current DSL
    */
  def throttle(perSecond: Long): BaseDSL[I] = ThrottlerDefinitionLong(perSecond)

  /**
    * The throttle keyword. Defines the maximal rate that may be use in order to avoid overloading of the rest of the route
    * @param perSecond number of allowed messages in 1 second using a function
    * @return the possibility to add other steps to the current DSL
    */
  def throttle(perSecond: Message[I] => Long): BaseDSL[I] = ThrottlerDefinitionExpression(MessageExpression(perSecond))
}

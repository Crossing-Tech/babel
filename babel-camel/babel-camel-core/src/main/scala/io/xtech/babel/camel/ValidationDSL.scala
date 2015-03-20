/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.camel.model.ValidationDefinition
import io.xtech.babel.fish._
import io.xtech.babel.fish.model.{ Message, Predicate }
import scala.reflect.ClassTag

private[camel] class ValidationDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The validate defines, using a function, how messages are validated.
    * An exception is thrown if the function returns false.
    * @param function which defines how a message is validated
    * @return the possibility to add other steps to the current DSL
    */
  def validate(function: Message[I] => Boolean): BaseDSL[I] = {
    ValidationDefinition(MessagePredicate(function))
  }

  /**
    * The validate defines, using a predicate (ex: simple language), how messages are validated.
    * An exception is thrown if the function returns false.
    * @param predicate which defines how a message is validated
    * @return the possibility to add other steps to the current DSL
    */
  def validate(predicate: Predicate[I]): BaseDSL[I] = {
    ValidationDefinition(predicate)
  }

  /**
    * The validate defines, using a function, how messages are validated.
    * An exception is thrown if the function returns false.
    * @param function which defines how a message is validated
    * @return the possibility to add other steps to the current DSL
    */
  def validateBody(function: I => Boolean): BaseDSL[I] = {
    ValidationDefinition(BodyPredicate(function))
  }
}

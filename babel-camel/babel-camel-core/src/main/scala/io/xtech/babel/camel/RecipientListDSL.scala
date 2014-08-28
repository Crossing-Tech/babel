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

package io.xtech.babel.camel

import io.xtech.babel.camel.model.RecipientListDefinition
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL, MessageExpression }
import io.xtech.babel.fish.model.{ Expression, Message }

import scala.reflect.ClassTag

private[camel] class RecipientListDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The recipientList defines, using a function, the endpoits where the inputs should be routed.
    * @param function which defines the targeted endpoints
    * @tparam E
    * @return the possibility to add other steps to the current DSL
    */
  def recipientList[E](function: Message[I] => E): BaseDSL[I] = {

    RecipientListDefinition(MessageExpression(function))
  }

  /**
    * The recipientList defines, using an expression, the endpoits where the inputs should be routed.
    * @param expression which defines the targeted endpoints
    * @tparam E type the targets (endpoints)
    * @return the possibility to add other steps to the current DSL
    */
  def recipientList[E](expression: Expression[I, E]): BaseDSL[I] = {

    RecipientListDefinition(expression)
  }

}

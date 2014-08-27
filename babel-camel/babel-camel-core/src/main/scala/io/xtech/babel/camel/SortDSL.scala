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

import io.xtech.babel.camel.model.SortDefinition
import io.xtech.babel.fish.{ DSL2BaseDSL, MessageExpression, BaseDSL }
import io.xtech.babel.fish.model.{ Expression, Message }

import java.util.Comparator
import java.util.{ List => JList }

import scala.collection.mutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the sort keyword to the BaseDSL.
  */
private[camel] class SortDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The sort keyword.
    * @param function what element of the message is sorted.
    * @param comparator how are the element sorted.
    * @tparam O the output type.
    * @return the possibility to add other steps to the current DSL
    */
  def sort[O: ClassTag](function: Message[I] => mutable.IndexedSeq[O], comparator: Comparator[O]): BaseDSL[JList[O]] = {

    SortDefinition(MessageExpression(function), Some(comparator))

  }

  /**
    * The sort keyword. This keyword use the default camel comparator.
    * @param function what element of the message is sorted.
    * @tparam O the output type.
    * @return the possibility to add other steps to the current DSL
    */
  def sort[O: ClassTag](function: Message[I] => mutable.IndexedSeq[O]): BaseDSL[JList[O]] = {

    SortDefinition(MessageExpression(function), None)

  }

  def sort[O: ClassTag, C](expression: Expression[I, O], comparator: Comparator[C]): BaseDSL[JList[O]] = {

    SortDefinition(expression, Some(comparator))

  }

  def sort[O: ClassTag](expression: Expression[I, O]): BaseDSL[JList[O]] = {

    SortDefinition(expression, None)

  }
}


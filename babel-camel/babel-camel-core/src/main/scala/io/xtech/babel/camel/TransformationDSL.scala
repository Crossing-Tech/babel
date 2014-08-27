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

import io.xtech.babel.camel.model.{ BeanClassExpression, BeanNameExpression, BeanObjectExpression }
import io.xtech.babel.fish.model.TransformerDefinition
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }

import scala.language.implicitConversions
import scala.reflect._

/**
  * Extentions to the base fish with extensions done with spring beans.
  * @param baseDsl the base fish.
  * @tparam I the message body type.
  */
private[camel] class TransformationDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  private def bean(beanRef: String, method: Option[String]): BaseDSL[Any] = {

    TransformerDefinition(BeanNameExpression(beanRef, method))
  }

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(str: String): BaseDSL[Any] = bean(str, None)

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(str: String, method: String): BaseDSL[Any] = bean(str, Some(method))

  private def bean(bean: AnyRef, method: Option[String]): BaseDSL[Any] = {

    TransformerDefinition(BeanObjectExpression(bean, method))

  }

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(ref: AnyRef): BaseDSL[Any] = bean(ref, None)

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(ref: AnyRef, method: String): BaseDSL[Any] = bean(ref, Some(method))

  private def bean(clazz: Class[_], method: Option[String]): BaseDSL[Any] = {

    TransformerDefinition(BeanClassExpression(clazz, method))

  }

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(clazz: Class[_]): BaseDSL[Any] = bean(clazz, None)

  @deprecated("bean keyword is deprecated as it removes type information for the next keyword. " +
    "To improve you route typing, please use a process or a processBody with a function.", "version 0.5.0")
  def bean(clazz: Class[_], method: String): BaseDSL[Any] = bean(clazz, Some(method))

}


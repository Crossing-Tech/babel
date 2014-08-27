/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

/**
  * extends a Java function be to read by Scala
  */
trait JVMFunction[-I, +O] {
  def apply(input: I): O
}

/**
  * extends a Java predicate to be read by Scala
  */
trait JVMPredicate[-I] {
  def apply(input: I): Boolean
}

/**
  * Facility tools for java 8 DSLs
  */
object ScalaHelper {

  def scalaFunction[I, O](function: JVMFunction[I, O]): (I => O) = (i) => {
    function.apply(i)
  }

  def scalaPredicate[I](predicate: JVMPredicate[I]): (I => Boolean) = (i) => {
    predicate.apply(i)
  }
}
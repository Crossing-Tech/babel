/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model.TransactionDefinition
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds transaction in the Bable Camel DSL.
  */
private[camel] class TransactionDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The transacted keyword. Defines that the exchange flow goes through a transaction.
    * @return the possibility to add other steps to the current DSL
    */
  def transacted(): BaseDSL[I] = TransactionDefinition()

  /**
    * The transacted keyword. Defines that the exchange flow goes through a transaction.
    * @param ref the bean which is responsible for the transaction.
    * @return the possibility to add other steps to the current DSL
    */
  //todo: missing tests for transacted
  def transacted(ref: String): BaseDSL[I] = TransactionDefinition(Some(ref))
}


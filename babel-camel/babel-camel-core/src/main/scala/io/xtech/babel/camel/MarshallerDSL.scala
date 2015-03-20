/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */
package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import org.apache.camel.spi.DataFormat
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * DSL adding the marshalling keywords.
  */
private[camel] class MarshallerDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The marshal keyword.
    * Defines how to transform inputs using a reference to a Bean.
    * @param ref reference of the bean used for the transformation
    * @return the possibility to add other steps to the current DSL
    */
  def marshal(ref: String): BaseDSL[Any] = {

    val props = MarshallerReference(ref)
    MarshallerDefinition(props)

  }

  /**
    * The marshal keyword.
    * Defines how to transform inputs using a dataFormat.
    * @param dataFormat used for the transformation
    * @return the possibility to add other steps to the current DSL
    */
  def marshal(dataFormat: DataFormat): BaseDSL[Any] = {

    val props = new MarshallerInstance(dataFormat)
    MarshallerDefinition(props)

  }

  /**
    * The unmarshal keyword.
    * Defines how to transform inputs using a reference to a Bean.
    * @param ref reference of the bean used for the transformation
    * @return the possibility to add other steps to the current DSL
    */
  def unmarshal(ref: String): BaseDSL[Any] = {

    val props = new UnmarshallerReference(ref)
    MarshallerDefinition(props)

  }

  /**
    * The unmarshal keyword.
    * Defines how to transform inputs using a dataFormat.
    * @param dataFormat used for the transformation
    * @return the possibility to add other steps to the current DSL
    */
  def unmarshal(dataFormat: DataFormat): BaseDSL[Any] = {

    val props = new UnmarshallerInstance(dataFormat)
    new MarshallerDefinition(props)

  }
}


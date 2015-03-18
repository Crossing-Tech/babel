/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model.CamelMessage
import io.xtech.babel.fish.model.Message
import org.apache.camel.{ Exchange, InvalidPayloadException, Processor }

/**
  * Wrapper for a scala function in a Camel Processor.
  * The function is used to process message bodies.
  * @param proc the function that process body messages.
  * @tparam I the input type of the function.
  * @tparam O the output type of the function.
  */
private[camel] class CamelBodyProcessor[I, O](proc: (I => O)) extends Processor {
  def process(exchange: Exchange): Unit = {

    val newBody = proc(exchange.getIn.getBody.asInstanceOf[I])
    exchange.getIn.setBody(newBody)
  }
}

/**
  * Wrapper for a scala function in a Camel Processor.
  * The function is used to process messages.
  * @param proc the function that process messages.
  * @tparam I the input type of the function.
  * @tparam O the output type of the function.
  */
private[camel] class CamelMessageProcessor[I, O](proc: (Message[I] => Message[O])) extends Processor {
  def process(exchange: Exchange): Unit = {

    val msg = new CamelMessage[I](exchange.getIn)
    proc(msg)
  }
}

private[camel] object CamelBodyTypeValidation {

  /**
    * Mapping between primitive types and wrapped types.
    * Used in order to accept, in requireAs, corresponding Scala types.
    */
  val primitiveToBoxed = Map[Class[_], Class[_]](
    classOf[Byte] -> classOf[java.lang.Byte],
    classOf[Short] -> classOf[java.lang.Short],
    classOf[Char] -> classOf[java.lang.Character],
    classOf[Int] -> classOf[java.lang.Integer],
    classOf[Long] -> classOf[java.lang.Long],
    classOf[Float] -> classOf[java.lang.Float],
    classOf[Double] -> classOf[java.lang.Double],
    classOf[Boolean] -> classOf[java.lang.Boolean],
    classOf[Unit] -> classOf[java.lang.Void]
  ).withDefault(identity)
}

/**
  * Processor that checks the validity of the body type
  * @param outputClass the required type.
  * @tparam O the required type
  */
private[camel] class CamelBodyTypeValidation[O](outputClass: Class[O]) extends Processor {

  def process(exchange: Exchange): Unit = {

    exchange.getIn.getMandatoryBody match {
      case c if outputClass.isPrimitive & CamelBodyTypeValidation.primitiveToBoxed(outputClass).isAssignableFrom(c.getClass) =>
      case c if outputClass.isAssignableFrom(c.getClass) =>
      case _ => throw new InvalidPayloadException(exchange, outputClass)
    }
  }
}

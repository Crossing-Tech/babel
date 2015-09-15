/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.parsing.Wiring
import io.xtech.babel.camel.model.{ CamelMessage, CamelSink, WireTapDefinition }
import io.xtech.babel.fish.model.{ Message, StepDefinition, TransformerDefinition }
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL, MessageTransformationExpression }

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds the wiretap keyword to the BaseDSL.
  */
private[camel] class WireTapDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * The wiretap keyword. Defines an endpoint where a copy of exchanges are forwarded, leaving the normal flow of the route.
    * @param uri of the endpoint which receives a copy of each exchange
    * @return the possibility to add other steps to the current DSL
    */
  def wiretap(uri: String): BaseDSL[I] = WireTapDefinition[I](CamelSink[I](uri))

  /**
    * The sideEffect lets you define a route as parameter that do not modify the output of the sideEffect keyword.
    * The side route is run sequentially and before the rest of the route.
    * @param w route which does not effect the rest of the route.
    * @return
    */
  def sideEffect(w: (BaseDSL[I]) => BaseDSL[_]): BaseDSL[I] = {
    val wireDefinition = new TransformerDefinition(wire)
    val dewireDefinition = new TransformerDefinition(dewire)

    baseDsl.step.next = Some(wireDefinition)
    val dsl = new BaseDSL[I](wireDefinition)

    w(dsl).step.next = Some(dewireDefinition)
    new BaseDSL[I](dewireDefinition)
  }

  private def propertyKey(count: Int): String = s"${Wiring.key}-${count + 1}"
  private def typeName(implicit ev: ClassTag[I]): String = ev.toString()

  private val wire: MessageTransformationExpression[I, I] = MessageTransformationExpression((msg: Message[I]) => {
    val headers = Wiring.getCount(msg)
    val wired = HeadersAndBody(msg.headers, msg.body)
    msg.asInstanceOf[CamelMessage[I]].withExchangeProperty(propertyKey(headers), wired) //TODO getOrElse
  })

  private val dewire: MessageTransformationExpression[I, I] = MessageTransformationExpression((msg: Message[I]) => {
    val key = s"${Wiring.key}-${Wiring.getCount(msg) + 1}"
    val wired: HeadersAndBody[I] = msg.asInstanceOf[CamelMessage[_]].exchangeProperties.get(key) match {
      case Some(w: HeadersAndBody[I]) => w
      case Some(other)                => throw new WiredUnexpectedBodyType(typeName, other.getClass.getName) //should never happen
      case None                       => throw new WiredEmptyException()
    }
    msg.withOptionalBody(_ => wired.body).withHeaders(_ => wired.headers).asInstanceOf[CamelMessage[I]].withExchangeProperties(_ - key)
  })

}

class WiringDefinition() extends StepDefinition

class WiredEmptyException extends IllegalArgumentException("Expected wired message but was empty in Babel sideEffect")
class WiredUnexpectedBodyType(expected: String, actual: String)
  extends IllegalArgumentException(s"Body has not expected type in Babel sideEffect: expected $expected, but was $actual")
case class HeadersAndBody[T](headers: Map[String, Any], body: Option[T])
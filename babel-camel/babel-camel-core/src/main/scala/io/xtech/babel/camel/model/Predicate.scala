/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ Message, Predicate }
import io.xtech.babel.fish.{ BodyPredicate, MessagePredicate }
import org.apache.camel.builder.xml.XPathBuilder
import org.apache.camel.{ Exchange, Predicate => CamelPredicate }
import scala.util.{ Failure, Success, Try }

/**
  * XPath defines a xpath used for an expression or a predicate.
  * @param str
  */
case class XPath(str: String) extends Predicate[Any]

/**
  * Wrapper for a scala function in a Camel Predicate.
  * @param predicate a predicate function.
  * @tparam I The type of the body message.
  */
class CamelMessagePredicate[I](predicate: (Message[I] => Boolean)) extends CamelPredicate {

  /**
    * @see org.apache.camel.Predicate
    */
  def matches(exchange: Exchange): Boolean = {
    val message = new CamelMessage[I](exchange.getIn)
    predicate(message)
  }
}

/**
  * Wrapper for a scala function in a Camel Predicate.
  * @param predicate a predicate function.
  * @tparam I The type of the body message.
  */
class CamelBodyPredicate[I](predicate: (I => Boolean)) extends CamelPredicate {

  /**
    * @see org.apache.camel.Predicate
    */
  def matches(exchange: Exchange): Boolean = Try {
    predicate(exchange.getIn.getBody.asInstanceOf[I])
  } match {
    case Success(result) =>
      result
    case Failure(ex) =>
      ex.printStackTrace()
      throw ex
  }

}

/**
  * Wrapper for a Camel Predicate.
  * Use to allow the use of Camel Predicate which is implicitly transformed into Babel Predicate.
  * @param predicate the real Camel Predicate
  */
case class CamelPredicateWrapper(predicate: org.apache.camel.Predicate) extends Predicate[Any]

/**
  * Facility object that provides a translation from a Babel predicate to a Camel one.
  */
object Predicates {

  def toCamelPredicate[I](predicate: Predicate[I]): CamelPredicate = {
    predicate match {
      case MessagePredicate(function)            => new CamelMessagePredicate(function)
      case BodyPredicate(function)               => new CamelBodyPredicate(function)
      case XPath(xpath)                          => XPathBuilder.xpath(xpath)
      case CamelPredicateWrapper(camelPredicate) => camelPredicate
      case _                                     => throw new Exception(s"unknown type of predicate : $predicate")
    }
  }
}

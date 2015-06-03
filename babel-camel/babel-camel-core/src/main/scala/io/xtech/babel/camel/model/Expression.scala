/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.{ Expression, Message }
import io.xtech.babel.fish.{ BodyExpression, MessageExpression }
import org.apache.camel.builder.xml.XPathBuilder
import org.apache.camel.{ Exchange, Expression => CamelExpression }

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * implicitely expands a Camel Expression to a Babel Expression
  * @param expression the Camel one.
  */
case class CamelExpressionWrapper(expression: org.apache.camel.Expression) extends Expression[Any, Any]

/**
  * translates a functional expression into a Camel expression
  */
case class CamelMessageExpression[I, O](function: (Message[I] => O)) extends CamelExpression {

  def evaluate[T](exchange: Exchange, t: Class[T]): T = {
    val message = new CamelMessage[I](exchange.getIn)
    function(message).asInstanceOf[T]
  }
}
/**
  * translates a functional expression on the message body into a Camel expression
  */
case class CamelBodyExpression[I, O](function: (I => O)) extends CamelExpression {

  def evaluate[T](exchange: Exchange, t: Class[T]): T = {
    val result = function(exchange.getIn.getBody.asInstanceOf[I])
    result.asInstanceOf[T]
  }
}

/**
  * An expression that converts the result of another expression to java.util.Iterator or java.util.Iterable if possible.
  * @param delegate another expression.
  */
class CamelIteratorExpression(delegate: CamelExpression) extends CamelExpression {
  def evaluate[T](exchange: Exchange, t: Class[T]): T = {

    val result = delegate.evaluate(exchange, t)

    result match {
      case it: Iterable[_] => it.asJava.asInstanceOf[T]
      case it: Iterator[_] => it.asJava.asInstanceOf[T]
      case r               => r.asInstanceOf[T]
    }
  }
}

/**
  * An expression that converts the result of another expression to java.util.list if possible.
  * @param delegate another expression.
  */
class CamelListExpression(delegate: CamelExpression) extends CamelExpression {
  def evaluate[T](exchange: Exchange, t: Class[T]): T = {

    val result = delegate.evaluate(exchange, t)

    result match {
      case s: mutable.IndexedSeq[_] => {
        s.asJava.asInstanceOf[T]
      }
      case r => {
        r.asInstanceOf[T]
      }
    }

  }
}

/**
  * expands a bean definition to an expression
  */
case class BeanNameExpression(beanRef: String, methodName: Option[String]) extends Expression[Any, Any]

/**
  * expands a bean object to an expression
  */
case class BeanObjectExpression(bean: AnyRef, methodName: Option[String]) extends Expression[Any, Any]

/**
  * expands a bean class to an expression
  */
case class BeanClassExpression[I](clazz: Class[I], methodName: Option[String]) extends Expression[Any, Any]

/**
  * A factory for creating Camel Expression from Babel expression.
  */
object Expressions {

  /**
    * Converts a Babel expression to a Camel expression.
    * @param expression a babel expression.
    * @tparam I the input type for the expression.
    * @tparam O the output type for the expression.
    * @return a Camel expression.
    */
  def toCamelExpression[I, O](expression: Expression[I, O]): CamelExpression = {
    expression match {
      case BodyExpression(function)                => CamelBodyExpression(function)
      case MessageExpression(function)             => CamelMessageExpression(function)
      case CamelExpressionWrapper(camelExpression) => camelExpression
      case XPath(xpath)                            => XPathBuilder.xpath(xpath)
      case _                                       => throw new Exception(s"unknown type of expression : $expression")
    }
  }

  /**
    * Converts a Babel expression to a Camel expression.
    * The evaluation of the expression will try to be a java.util.Iterator or java.util.Iterable
    * @param expression a babel expression.
    * @tparam I the input type for the expression.
    * @tparam O the output type for the expression.
    * @return a Camel expression.
    */
  def toJavaIteratorCamelExpression[I, O](expression: Expression[I, O]): CamelExpression = {
    val camelExpression = toCamelExpression(expression)

    new CamelIteratorExpression(camelExpression)
  }

  /**
    * Converts a Babel expression to a Camel expression.
    * The evaluation of the expression will try to be a java.util.List
    * @param expression a babel expression.
    * @tparam I the input type for the expression.
    * @tparam O the output type for the expression.
    * @return a Camel expression.
    */
  def toJavaListCamelExpression[I, O](expression: Expression[I, O]): CamelExpression = {
    val camelExpression = toCamelExpression(expression)

    new CamelListExpression(camelExpression)
  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.Message
import org.apache.camel.{ ExchangePattern, Message => NativeMessage }
import scala.collection.JavaConverters._
import scala.reflect._

/**
  * An immutable wrapper for a camel message.
  * @param message the original camel message.
  * @tparam I the type of the message body.
  */
class CamelMessage[I](message: NativeMessage) extends Message[I] {

  validateCamelMessage(message)

  private[this] def validateCamelMessage(msg: NativeMessage): Unit = {
    require(msg != null, "a camel message is mandatory")
    // TODO needs final version of scala reflection
  }

  def body: Option[I] = Option(message.getBody.asInstanceOf[I])

  def bodyAs[A: ClassTag]: Option[A] = {
    val clazz = classTag[A].runtimeClass.asInstanceOf[Class[A]]
    val body = message.getBody(clazz)
    Option(body)
  }

  def withBody[O: ClassTag](f: (I) => O): Message[O] = {
    val body = message.getBody().asInstanceOf[I]
    val newBody = f(body)
    message.setBody(newBody)
    new CamelMessage[O](message)
  }

  def withOptionalBody[O: ClassTag](f: (Option[I]) => Option[O]): Message[O] = {
    val body = Option(message.getBody()).map(b => b.asInstanceOf[I])
    val newBody = f(body)
    message.setBody(newBody.getOrElse(null))
    new CamelMessage[O](message)
  }

  def headers: Map[String, Any] = {
    message.getHeaders.asScala.toMap
  }

  def withHeader(key: String, value: Any): Message[I] = {
    message.setHeader(key, value)
    new CamelMessage[I](message)
  }

  def withHeaders(f: (Map[String, Any]) => Map[String, Any]): Message[I] = {
    val headers = message.getHeaders.asScala.toMap
    val newHeaders = f(headers)
    message.setHeaders(newHeaders.asJava.asInstanceOf[java.util.Map[String, java.lang.Object]])
    new CamelMessage[I](message)
  }

  /**
    * @return an immutable Map of the properties of the Camel Exchange which is represented by this CamelMessage.
    */
  def exchangeProperties: Map[String, Any] = {
    message.getExchange.getProperties.asScala.toMap
  }

  /**
    * Creates or updates a property in the wrapped Camel Exchange.
    * @param key the key.
    * @param value the value.
    * @return a new Message with the new exchange property.
    */
  def withExchangeProperty(key: String, value: Any): CamelMessage[I] = {
    message.getExchange.setProperty(key, value)
    new CamelMessage[I](message)
  }

  /**
    * Changes the Camel Exchange properties with a function.
    * @param f a function processing the properties of the wrapped Camel Exchange.
    * @return a new Message with modified properties.
    */
  def withExchangeProperties(f: (Map[String, Any]) => Map[String, Any]): CamelMessage[I] = {
    val properties = message.getExchange.getProperties.asScala.toMap
    val newProperties = f(properties)
    message.getExchange.getProperties.keySet().asScala.foreach(p => message.getExchange.removeProperty(p))
    newProperties.foreach(p => message.getExchange.setProperty(p._1, p._2))
    new CamelMessage[I](message)
  }

  /**
    * @return the exception contained in the wrapped Camel Exchange.
    */
  def exchangeException: Exception = {
    message.getExchange.getException
  }

  /**
    * Override the exception of the wrapped Camel Exchange.
    * @param e to be added to the Camel Exchange
    * @return a new Message with the exception
    */
  def withExchangeException(e: Exception): CamelMessage[I] = {
    message.getExchange.setException(e)
    new CamelMessage[I](message)
  }

  /**
    * @return return the wrapped Camel Exchange pattern (MEP)
    */
  def exchangePattern: ExchangePattern = {
    message.getExchange.getPattern
  }

  /**
    * Set or overrides the wrapped Camel Exchange pattern (MEP)
    * @param mep new exchange pattern of the wrapped Camel Exchange
    * @return a new Message with the Exchange Pattern
    */
  def withExchangePattern(mep: ExchangePattern): CamelMessage[I] = {
    message.getExchange.setPattern(mep)
    new CamelMessage[I](message)
  }

  //the link to the wrapped exchange is cut as main functionnalities are provided directly in the current class.
  //def nativeMessage = message
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model.Message

import scala.reflect._

/**
  * An immutable wrapper for a Mediation engine message.
  * @tparam I the type of the message body.
  */
class TestMessage[I: ClassTag] extends Message[I] {

  /**
    * Gets the message body.
    * @return an object of type I.
    */
  def body: Option[I] = ???

  /**
    * Gets an converts the message body.
    * @tparam A the type of the returned object.
    * @return an object of type A.
    */
  def bodyAs[A: ClassTag]: Option[A] = ???

  /**
    * Changes the message body.
    * @param f the function that changes the body.
    * @tparam O the new type of the body.
    * @return a new message.
    */
  def withBody[O: ClassTag](f: (I => O)): TestMessage[O] = ???

  def withOptionalBody[O: ClassTag](f: (Option[I]) => Option[O]): Message[O] = ???

  /**
    * Gets the message headers.
    * @return a map with the headers.
    */
  def headers: Map[String, Any] = ???

  /**
    * Changes a header of the message.
    * @param key the key of the header.
    * @param value the new value of the header.
    * @return a new message.
    */
  def withHeader(key: String, value: Any): TestMessage[I] = ???

  /**
    * Changes the message headers with a function.
    * @param f a function processing the headers of the message.
    * @return a new Message with modified headers.
    */
  def withHeaders(f: (Map[String, Any]) => Map[String, Any]): Message[I] = ???
}

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

package io.xtech.babel.fish.model

import scala.reflect.ClassTag

/**
  * The source of a route.
  * @tparam O
  */
trait Source[+O]

/**
  * The destination of a route.
  * @tparam I
  * @tparam O
  */
trait Sink[-I, +O]

/**
  * The base case for babel predicate.
  * @tparam I the input type.
  */
trait Predicate[-I] extends Expression[I, Boolean]

/**
  * The base class for babel expression.
  * @tparam I the input type.
  * @tparam O the output type.
  */
trait Expression[-I, +O]

/**
  * A message passing through a route.
  * It is immutable and all changes create a new message.
  * @tparam T the type of the body.
  */
trait Message[+T] {

  /**
    * Returns the body message.
    */
  def body: Option[T]

  /**
    * Returns the body converted to a new type.
    * @tparam A the new type.
    * @return the converted body.
    */
  def bodyAs[A: ClassTag]: Option[A]

  /**
    * Changes the body message with a function.
    * This method requires a body.
    * @param f the function that process the body.
    * @tparam O the type taken by the body after the change.
    * @return a new message with the new body.
    */
  //todo should we manage the potential NPE as body may be null, or let this problem to the user?
  def withBody[O: ClassTag](f: (T => O)): Message[O]

  /**
    * Changes the body message with a function.
    * This method let you handle the case when the body don't exist.
    * @param f the function that process the body.
    * @tparam O the type taken by the body after the change.
    * @return a new message with the new body.
    */
  def withOptionalBody[O: ClassTag](f: (Option[T] => Option[O])): Message[O]

  /**
    * Returns an immutable Map of all headers in this message.
    * @return an immutable Map with all the headers.
    */
  def headers: Map[String, Any]

  /**
    * Creates or updates a header in the message.
    * @param key the key.
    * @param value the value.
    * @return a new Message with the new header.
    */
  def withHeader(key: String, value: Any): Message[T]

  /**
    * Changes the message headers with a function.
    * @param f a function processing the headers of the message.
    * @return a new Message with modified headers.
    */
  def withHeaders(f: (Map[String, Any] => Map[String, Any])): Message[T]
}
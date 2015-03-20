/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.Sink
import scala.collection.immutable

/**
  * Sink of a Camel route, implicitly created when calling {{{ to("uri") }}}
  * @param uri given to the `to` keyword
  */
case class CamelSink[-I](uri: String) extends Sink[I, Any]

/**
  * An utility object used in matchers to extract a Seq of a CamelSink from a Seq of Sink
  * Used in the multicast
  */
object SeqCamelSink {
  def unapplySeq(m: immutable.Seq[Sink[_, _]]) = {

    val t = m.collect {
      case s: CamelSink[_] => s
    }

    Some(t)
  }
}

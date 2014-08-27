/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.Source

/**
  * Source of a Camel route, implicitly created when calling {{{ from("uri") }}}
  * @param uri given to the `from` keyword
  */
case class CamelSource(uri: String) extends Source[Any]

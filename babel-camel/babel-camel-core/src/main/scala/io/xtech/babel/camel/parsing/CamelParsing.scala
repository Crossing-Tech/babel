/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.fish.parsing.Parsing
import org.apache.camel.builder.RouteBuilder

/**
  * The CamelParsing is the parent trait of each keyword parser in babel-camel.
  */
protected[camel] trait CamelParsing extends Parsing[RouteBuilder]

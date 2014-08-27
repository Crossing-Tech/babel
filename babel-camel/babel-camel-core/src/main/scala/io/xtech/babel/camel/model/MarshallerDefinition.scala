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

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.StepDefinition

import org.apache.camel.spi.DataFormat

/**
  * defines a Marshall EIP.
  */
trait MarshallerProps

case class MarshallerInstance(dataFormat: DataFormat) extends MarshallerProps

case class MarshallerReference(ref: String) extends MarshallerProps

case class UnmarshallerInstance(dataFormat: DataFormat) extends MarshallerProps

case class UnmarshallerReference(ref: String) extends MarshallerProps

case class MarshallerDefinition(val props: MarshallerProps) extends StepDefinition

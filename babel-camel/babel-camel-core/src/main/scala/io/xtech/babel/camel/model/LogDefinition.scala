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

import org.apache.camel.LoggingLevel

/**
  * Defines the configuration of a log EIP.
  *
  */
trait LogProps {
  def output: String
}

/**
  * defines a log with its output
  */
case class LogMessage(output: String) extends LogProps

/**
  * defines a log with its logging level and its output
  */
case class LogLoggingLevelMessage(level: LoggingLevel, output: String) extends LogProps

/**
  * defines a log with its logging level, its log name and its output
  */
case class LogLoggingLevelLogNameMessage(level: LoggingLevel, LogName: String, output: String) extends LogProps

/**
  * defines a log with its logging level, its log name, a marker and its output
  */
case class LogLoggingLevelLogNameMarkerMessage(level: LoggingLevel, LogName: String, marker: String, output: String) extends LogProps

/**
  * Defines a Log EIP.
  * @param props the logging information.
  * @see io.xtech.babel.camel.LogProps
  */
case class LogDefinition(props: LogProps) extends StepDefinition

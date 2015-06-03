/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import org.apache.camel.LoggingLevel

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * DSL adding the log keyword.
  */
private[camel] class LogDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  /**
    * the log keyword.
    * @param output defines what is displayed in the logs
    * @return the possibility to add other steps to the current DSL
    */
  def log(output: String): BaseDSL[I] = {
    LogDefinition(LogMessage(output))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param output defines what is displayed in the logs
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, output: String): BaseDSL[I] = {
    new LogDefinition(LogLoggingLevelMessage(logLevel, output))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param logName is used by the log engine to select which logging appender should be used for this log
    * @param output defines what is displayed in the logs
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, logName: String, output: String): BaseDSL[I] = {
    LogDefinition(LogLoggingLevelLogNameMessage(logLevel, logName, output))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param logName is used by the log engine to select which logging appender should be used for this log
    * @param marker is used to "tag" this log
    * @param output defines what is displayed in the logs
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, logName: String, marker: String, output: String): BaseDSL[I] = {
    LogDefinition(LogLoggingLevelLogNameMarkerMessage(logLevel, logName, marker, output))
  }
}


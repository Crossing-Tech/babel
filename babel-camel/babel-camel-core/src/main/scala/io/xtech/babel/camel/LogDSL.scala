/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.fish.model.Message
import io.xtech.babel.fish.{ BaseDSL, DSL2BaseDSL }
import org.apache.camel.LoggingLevel
import org.apache.camel.language.simple.SimpleLanguage

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * DSL adding the log keyword.
  */
private[camel] class LogDSL[I: ClassTag](protected val baseDsl: BaseDSL[I]) extends DSL2BaseDSL[I] {

  private val bodyExpression: String = "${body}"
  private def applyPreparation(prepare: (Message[I]) => String): (Message[I] => Message[String]) = msg => msg.withBody(_ => prepare(msg))
  private def preprocess(prepare: (Message[I]) => String, log: (LogDSL[Any]) => BaseDSL[_]): BaseDSL[I] = {
    new WireTapDSL[I](baseDsl.step).sideEffect(dsl => log(new LogDSL(dsl.process(applyPreparation(prepare)))))
  }

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

  /**
    * the log keyword.
    * @param prepare defines how the message is prepared to be logged
    * @return the possibility to add other steps to the current DSL
    */
  def log(prepare: (Message[I]) => String): BaseDSL[I] = {
    preprocess(prepare, _.log(bodyExpression))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param prepare defines how the message is prepared to be logged
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, prepare: (Message[I]) => String): BaseDSL[I] = {
    preprocess(prepare, _.log(logLevel, bodyExpression))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param logName is used by the log engine to select which logging appender should be used for this log
    * @param prepare defines how the message is prepared to be logged
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, logName: String, prepare: (Message[I]) => String): BaseDSL[I] = {
    preprocess(prepare, _.log(logLevel, logName, bodyExpression))
  }

  /**
    * the log keyword.
    * @param logLevel defines the minimal LoggingLevel required to let this output been shown in the logs
    * @param logName is used by the log engine to select which logging appender should be used for this log
    * @param marker is used to "tag" this log
    * @param prepare defines how the message is prepared to be logged
    * @return the possibility to add other steps to the current DSL
    */
  def log(logLevel: LoggingLevel, logName: String, marker: String, prepare: (Message[I]) => String): BaseDSL[I] = {
    preprocess(prepare, _.log(logLevel, logName, marker, bodyExpression))
  }
}


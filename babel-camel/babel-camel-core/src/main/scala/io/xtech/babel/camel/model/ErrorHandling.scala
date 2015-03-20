/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model._
import org.apache.camel.LoggingLevel
import org.apache.camel.builder._
import org.apache.camel.processor.RedeliveryPolicy
import org.apache.camel.spring.spi.TransactionErrorHandlerBuilder
import org.slf4j.Logger

/**
  * container for error handling configuration
  */
case class HandlerDefinition() extends ScopeDefinition[StepDefinition]

/**
  * base trait for error handlers.
  * Provides a transalation to a Camel error handler builder, which should build the concerned Camel error handler
  */
trait ErrorHandling {
  /**
    * Defines the way to translate to a Camel error handler builder which may build such error handler.
    * @return an errorhandlerbuilder to set camel errorhandling configuration
    */
  protected[camel] def camelErrorHandlerBuilder: ErrorHandlerBuilder
}

/**
  * NoErrorHandler keyword configuration
  */
object NoErrorHandlerDefinition extends StepDefinition with ErrorHandling {
  def camelErrorHandlerBuilder: ErrorHandlerBuilder = new NoErrorHandlerBuilder()
}

/**
  * LoggingErrorHandler keyword configuration
  */
class LoggingErrorHandlerDefinition(log: Logger, level: LoggingLevel) extends StepDefinition with ErrorHandling {
  def camelErrorHandlerBuilder: ErrorHandlerBuilder = new LoggingErrorHandlerBuilder(log, level)
}

/**
  * trait for error handler which allow exchange redelivery on failure.
  */
trait RedeliveryErrorHandling extends ErrorHandling {
  protected def routeErrorHandler(): DefaultErrorHandlerBuilder

  private[this] var _redeliveryPolicy: Function[RedeliveryPolicy, RedeliveryPolicy] = (x: RedeliveryPolicy) => x

  def camelErrorHandlerBuilder: ErrorHandlerBuilder = {
    val handler = routeErrorHandler()
    handler.setRedeliveryPolicy(_redeliveryPolicy(handler.getRedeliveryPolicy()))
    handler
  }

  /**
    * Allow the configuration of the redelivery policy of the current error handler.
    * @param conf to be added to the error handling.
    * @return the possibility to add other configuration
    */
  private[camel] def redeliveryPolicy(conf: Function[RedeliveryPolicy, RedeliveryPolicy]): ErrorHandling = {
    //todo might be directly applied on the redeliverypolicy ?
    _redeliveryPolicy = _redeliveryPolicy.andThen(conf)
    this
  }
}

/**
  * DefaultErrorHandler keyword configuration
  */
class DefaultErrorHandlerDefinition() extends StepDefinition with RedeliveryErrorHandling {
  protected def routeErrorHandler: DefaultErrorHandlerBuilder = new DefaultErrorHandlerBuilder()
}

/**
  * DeadLetterChannel keyword configuration
  */
class DeadLetterDefinition(sink: String) extends StepDefinition with RedeliveryErrorHandling {
  protected def routeErrorHandler: DefaultErrorHandlerBuilder = new DeadLetterChannelBuilder(sink)
}

/**
  * TransactionErrorHandler keyword configuration
  */
class TransactionErrorHandlerDefinition extends StepDefinition with RedeliveryErrorHandling {
  protected def routeErrorHandler: DefaultErrorHandlerBuilder = new TransactionErrorHandlerBuilder()
}

/**
  * Configuration of a onException keyword
  * @param exception which Exception type is handled by this keyword
  * @param when defines whenever the exception should be treated by this exception clause or not
  */
case class OnExceptionDefinition[T <: Throwable](exception: Class[T],
                                                 when: Option[Predicate[Any]] = None) extends StepDefinition {
  private[this] var predicate: Option[(Boolean, Predicate[Any])] = None

  /**
    * used by the *handled* and the *handledMessage* keywords to set an handled predicate
    * predicate may not be field more than once, this is enforced through the DSL typing.
    * @return itself with the new predicate
    */
  def handle(predicate: Predicate[Any]): OnExceptionDefinition[T] = {
    this.predicate = Some((false, predicate))
    this
  }

  /**
    * used by the *continued* and the *continuedMessage* keywords to set an continued predicate
    * predicate may not be field more than once, this is enforced through the DSL typing.
    * @return itself with the new predicate
    */
  def continue(predicate: Predicate[Any]): OnExceptionDefinition[T] = {
    this.predicate = Some((true, predicate))
    this
  }

  /**
    * Configures the behaviour of a Camel OnExceptionDefintion
    * Should be called only once the continue or handle predicate got specified if any.
    * @param processor the Camel OnExceptionDefinition to configure
    */
  def applyToCamel(processor: org.apache.camel.model.OnExceptionDefinition): Unit = {
    when.foreach(predicate => processor.onWhen(Predicates.toCamelPredicate(predicate)))

    predicate match {
      case Some((true, predicate)) =>
        processor.continued(Predicates.toCamelPredicate(predicate))
      case Some((false, predicate)) =>
        processor.handled(Predicates.toCamelPredicate(predicate))
      case None =>
    }
  }
}
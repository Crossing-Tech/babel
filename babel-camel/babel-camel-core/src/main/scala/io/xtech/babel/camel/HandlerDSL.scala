/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.fish.model.{ Message, Predicate }
import io.xtech.babel.fish.{ BodyPredicate, _ }
import org.apache.camel.LoggingLevel
import org.apache.camel.processor.RedeliveryPolicy
import org.slf4j.{ Logger, LoggerFactory }

import scala.language.implicitConversions
import scala.reflect.ClassTag

private[camel] class GlobalDSL(override val step: EmptyDefinition = new EmptyDefinition) extends FromDSL[Any](step)

/**
  * DSL which allows the use of the handle keyword, which state the handling of exception
  *
  * @param from
  * @tparam I
  */
private[camel] class HandlerDSL[I: ClassTag](from: FromDSL[I]) {
  protected[camel] val definition = new HandlerDefinition()

  def handle(block: HandlingDSL[I] => Unit): BaseDSL[I] = {
    from.step.next = Some(definition)

    val dsl = new HandlingDSL[I](this)
    block(dsl)

    new FromDSL[I](definition)
  }

}

/**
  * DSL which allows to define onException and error handler keywords
  * This DSL may be used right after the from or directly in the RouteBuilder for global error handling
  * @param handler
  * @tparam I
  */
private[camel] class HandlingDSL[I: ClassTag](handler: HandlerDSL[I]) {

  /**
    * The on keyword defines an Exception Clause to handle exceptions base on its type.
    *
    * @param ev
    * @tparam T type of the handled exception
    * @return the possibility to add other steps to the current DSL which allows specifiction of the exception handling
    */
  def on[T <: Throwable](implicit ev: Manifest[T]): OnExceptionDSL = {
    val definition = OnExceptionDefinition[T](ev.runtimeClass.asInstanceOf[Class[T]], None)
    //todo connect before and after handling
    handler.definition.scopedSteps +:= definition

    new OnExceptionDSL(new BaseDSL[Any](definition), definition)
  }

  /**
    * The on keyword defines an Exception Clause to handle exceptions base on its type.
    * @param when is use to define more precisely when an exception should be handled through the current clause.
    * @param ev
    * @tparam T type of the handled exception
    * @return the possibility to add other steps to the current DSL which allows specifiction of the exception handling
    */
  def onBody[T <: Throwable](when: BodyPredicate[Any])(implicit ev: Manifest[T]): OnExceptionDSL = {
    val definition = OnExceptionDefinition[T](ev.runtimeClass.asInstanceOf[Class[T]], Some(when))
    //todo connect before and after handling
    handler.definition.scopedSteps +:= definition

    new OnExceptionDSL(new BaseDSL[Any](definition), definition)
  }

  /**
    * The on keyword defines an Exception Clause to handle exceptions base on its type.
    * @param when is use to define more precisely when an exception should be handled through the current clause.
    * @param ev
    * @tparam T type of the handled exception
    * @return the possibility to add other steps to the current DSL which allows specifiction of the exception handling
    */
  def on[T <: Throwable](when: MessagePredicate[Any])(implicit ev: Manifest[T]): OnExceptionDSL = {
    val definition = OnExceptionDefinition[T](ev.runtimeClass.asInstanceOf[Class[T]], Some(when))
    //todo connect before and after handling
    handler.definition.scopedSteps +:= definition

    new OnExceptionDSL(new BaseDSL[Any](definition), definition)
  }

  /**
    * The on keyword defines an Exception Clause to handle exceptions base on its type.
    * @param when is use to define more precisely when an exception should be handled through the current clause.
    * @param ev
    * @tparam T type of the handled exception
    * @return the possibility to add other steps to the current DSL which allows specifiction of the exception handling
    */
  def onBody[T <: Throwable](when: Function[Any, Boolean])(implicit ev: Manifest[T]): OnExceptionDSL = {
    onBody(BodyPredicate(when))
  }

  /**
    * The on keyword defines an Exception Clause to handle exceptions base on its type.
    * @param when is use to define more precisely when an exception should be handled through the current clause.
    * @param ev
    * @tparam T type of the handled exception
    * @return the possibility to add other steps to the current DSL which allows specifiction of the exception handling
    */
  def on[T <: Throwable](when: Function[Message[Any], Boolean])(implicit ev: Manifest[T]): OnExceptionDSL = {
    on(MessagePredicate(when))
  }

  /**
    * The defaultErrorHandler defines error handling.
    *
    * @return a sub DSL to configure the error handling
    */
  def defaultErrorHandler: RedeliveryDSL = {
    val definition = new DefaultErrorHandlerDefinition

    handler.definition.scopedSteps +:= definition

    new RedeliveryDSL(definition)
  }

  /**
    * The Deadletter defines error handling through the use of a DeadLetter
    *
    * @param to the endpoint which receives erroneous exchanges
    * @return a sub DSL to configure the error handling.
    */
  def deadletter(to: String): RedeliveryDSL = {
    val definition = new DeadLetterDefinition(to)

    handler.definition.scopedSteps +:= definition

    new RedeliveryDSL(definition)
  }

  /**
    * The noErrorHandler overrides the error handling configuration inherited through parent routes.
    *
    * @return the end of current DSL statement
    */
  def noErrorHandler: NoDSL = {
    val definition = NoErrorHandlerDefinition

    handler.definition.scopedSteps +:= definition

    new NoDSL {}
  }

  /**
    * The loggingErrorHandler defines error handling through logging.
    *
    * @param logger used to issue errors
    * @param level at which the errors are prompted
    * @return the end of the current DSL statement
    */
  def loggingErrorHandler(logger: Logger = LoggerFactory.getLogger(classOf[Logger]),
                          level: LoggingLevel = LoggingLevel.INFO): NoDSL = {
    val definition = new LoggingErrorHandlerDefinition(logger, level)

    handler.definition.scopedSteps +:= definition

    new NoDSL {}
  }

  /**
    * The transactionErrorHandler defines error handling over transaction.
    *
    * @see transacted keyword
    * @return a sub DSL to configure the error handling
    */
  def transactionErrorHandler: RedeliveryDSL = {
    val definition = new TransactionErrorHandlerDefinition()

    handler.definition.scopedSteps +:= definition

    new RedeliveryDSL(definition)
  }

}

/**
  * This DSL allows to configure error handlers which are supporting redelivery
  * @param handling
  */
private[camel] class RedeliveryDSL(handling: RedeliveryErrorHandling) {

  /**
    * Configures the error handling by setting maximal redelivery number.
    *
    * @param count of redeliveries at most
    * @see  org.apache.camel.processor.RedeliveryPolicy.maximumRedeliveries
    */
  def maximumRedeliveries(count: Int): Unit = {
    handling.redeliveryPolicy((x: RedeliveryPolicy) => x.maximumRedeliveries(count))
  }

  /**
    * Configures the error handling by setting the initial redelivery delay.
    * @param delay initial for redelivery
    * @see  org.apache.camel.processor.RedeliveryPolicy.redeliveryDelay
    */
  def redeliveryDelay(delay: Long): Unit = {
    handling.redeliveryPolicy((x: RedeliveryPolicy) => x.redeliveryDelay(delay))
  }

  /**
    * Configures the error handling by setting maximal redelivery delay.
    * @param delay max for redelivery
    * @see  org.apache.camel.processor.RedeliveryPolicy.maximumRedeliveryDelay
    */
  def maximumRedeliveryDelay(delay: Long): Unit = {
    handling.redeliveryPolicy((x: RedeliveryPolicy) => x.maximumRedeliveryDelay(delay))
  }
}

/**
  * This DSL allows to configure the exception clause to allow exchange to be set as Handled or Continued (exclusively).
  * @param baseDsl containing the current OnExceptionDefinition
  * @param onException the current OnExceptionDefinition which may be enriched using this sub DSL.
  */
private[camel] class OnExceptionDSL(baseDsl: BaseDSL[Any], onException: OnExceptionDefinition[_]) extends SubRouteDSL[Any](baseDsl) {
  /**
    * Defines that the exchange should continue the normal flow if corresponding to some Predicate on its Body.
    * @param predicate which allows the exchange to keep going on depending on the original message body (before the exception)
    * @return the ability to define a sub route that also receives the exchange
    */
  def continuedBody(predicate: BodyPredicate[Any]): SubRouteDSL[Any] = {
    onException.continue(predicate)
    new SubRouteDSL[Any](baseDsl)
  }

  /**
    * Defines that the exchange should continue the normal flow if corresponding to some Predicate on the Message.
    * @param predicate which allows the exchange to keep going on depending on the original message (before the exception)
    * @return the ability to define a sub route that also receives the exchange
    */
  def continued(predicate: Predicate[Any]): SubRouteDSL[Any] = {
    onException.continue(predicate)
    new SubRouteDSL[Any](baseDsl)
  }

  /**
    * Defines that the exchange should continue the normal flow if corresponding to some Predicate on its Body.
    * @param function which allows the exchange to keep going on depending on the original message body (before the exception)
    * @return the ability to define a sub route that also receives the exchange
    */
  def continuedBody(function: Function[Any, Boolean]): SubRouteDSL[Any] = {
    continuedBody(BodyPredicate(function))
  }

  /**
    * Defines that the exchange should continue the normal flow if corresponding to some Predicate on the Message.
    * @param function which allows the exchange to keep going on depending on the original message (before the exception)
    * @return the ability to define a sub route that also receives the exchange
    */
  def continued(function: Function[Message[Any], Boolean]): SubRouteDSL[Any] = {
    continued(MessagePredicate(function))
  }

  /**
    * Defines that the exchange should be handled if corresponding to some Predicate on its Body.
    * @param predicate which allows the exchange exception to be tagged as handled depending on the original message body (before the exception)
    * @return the ability to define a sub route that receives the exchange
    */
  def handledBody(predicate: BodyPredicate[Any]): SubRouteDSL[Any] = {
    onException.handle(predicate)
    new SubRouteDSL[Any](baseDsl)
  }

  /**
    * Defines that the exchange should be handled if corresponding to some Predicate on the Message.
    * @param predicate which allows the exchange exception to be tagged as handled depending on the original message (before the exception)
    * @return the ability to define a sub route that receives the exchange
    */
  def handled(predicate: MessagePredicate[Any]): SubRouteDSL[Any] = {
    onException.handle(predicate)
    new SubRouteDSL[Any](baseDsl)
  }

  /**
    * Defines that the exchange should be handled if corresponding to some Predicate on its Body.
    * @param function which allows the exchange exception to be tagged as handled depending on the original message body (before the exception)
    * @return the ability to define a sub route that receives the exchange
    */
  def handledBody(function: Function[Any, Boolean]): SubRouteDSL[Any] = {
    handledBody(BodyPredicate(function))
  }

  /**
    * Defines that the exchange should be handled if corresponding to some Predicate on the Message.
    * @param function which allows the exchange exception to be tagged as handled depending on the original message (before the exception)
    * @return the ability to define a sub route that receives the exchange
    */
  def handled(function: Function[Message[Any], Boolean]): SubRouteDSL[Any] = {
    handled(MessagePredicate(function))
  }

}


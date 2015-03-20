/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.HandlerDSL
import io.xtech.babel.camel.model.{ ChannelDefinition, ErrorHandling, OnExceptionDefinition }
import io.xtech.babel.fish.parsing.StepInformation
import io.xtech.babel.fish.{ BodyPredicate, FromDSL }
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.ProcessorDefinition
import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The Exception Handler parser.
  */
private[babel] trait Handler extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def handlerDSLExtension[I: ClassTag](baseDsl: FromDSL[I]): HandlerDSL[I] = new HandlerDSL(baseDsl)

  //used to allow user to define a predicate on exceptions from a boolean
  implicit def booleanAsPredicate[Any](bool: Boolean): BodyPredicate[Any] = {
    BodyPredicate[Any](_ => bool)
  }

  private[this] def parseOnException[T <: Throwable, I](exception: OnExceptionDefinition[T], processor: org.apache.camel.model.OnExceptionDefinition): Unit = {
    //Warning: predicates and functions here may cause Camel to fail silently (without printing any exception)

    exception.applyToCamel(processor)

    //parse the subroute if any
    exception.next match {
      case Some(channel: ChannelDefinition) =>
        processor.to(channel.channelUri)
      case other => //no need to add a subroute
    }
    processor.end()
  }

  /**
    * parses the error handling keyword, at both level : Route and RouteBuilder
    */
  private[this] def parse: Process = {

    //handling at Route level
    case s @ StepInformation(exception: OnExceptionDefinition[_], camelProcessorDefinition: ProcessorDefinition[_]) =>
      parseOnException(exception, camelProcessorDefinition.onException(exception.exception))
      s.buildHelper //if sub exists for this onException, it should be parsed specifically...

    case s @ StepInformation(handler: ErrorHandling, camelProcessorDefinition: ProcessorDefinition[_]) =>
      s.buildHelper.getRouteCollection.getRoutes.asScala.lastOption.map(_.setErrorHandlerBuilder(handler.camelErrorHandlerBuilder))
      camelProcessorDefinition

    //handling at RouteBuilder level
    case s @ StepInformation(exception: OnExceptionDefinition[_], _: RouteBuilder) =>
      val camelProcessor = s.buildHelper.onException(exception.exception)
      parseOnException(exception, camelProcessor)
      (camelProcessor, s.buildHelper)

    case s @ StepInformation(handler: ErrorHandling, _: RouteBuilder) =>
      s.buildHelper.errorHandler(handler.camelErrorHandlerBuilder)
      s.buildHelper

  }
}

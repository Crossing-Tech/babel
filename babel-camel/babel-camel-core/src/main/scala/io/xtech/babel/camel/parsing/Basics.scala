/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.model._
import io.xtech.babel.camel.{ CamelDSL, SubRouteDSL }
import io.xtech.babel.fish.BaseDSL
import io.xtech.babel.fish.model._
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.ExchangePattern
import org.apache.camel.model.ProcessorDefinition
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Basics is the main parsing trait. It contains the main (or most basic) keywords parsing.
  * It is enriched by the other CamelParsing traits to provides to the CamelDSL all the possible `steps`.
  */
private[babel] trait Basics extends CamelParsing { self: CamelDSL =>

  implicit def subRouteDSLExtension[I: ClassTag](baseDsl: BaseDSL[I]): SubRouteDSL[I] = new SubRouteDSL(baseDsl)

  def steps: immutable.Seq[Process] = immutable.Seq(from,
    handle,
    subs,
    handler,
    endpointImplementation,
    bodyConvertor,
    choice,
    splitter,
    filter,
    multicast,
    bodyTypeValidation)

  private[this] def from: Process = {
    case s @ StepInformation(FromDefinition(_, CamelSource(uri)), _) => {

      s.buildHelper.from(uri)
    }
  }

  private[this] def handle: Process = {
    case s @ StepInformation(_: EmptyDefinition, camelProcessor) => {

      s.buildHelper
    }
  }

  //warning need to copy the code of from and routeId parsing
  private[this] def subs: Process = {
    case step @ StepInformation(d: ChannelDefinition, camelProcessor) => {
      //end route
      camelProcessor match {
        case processor: ProcessorDefinition[_] =>
          processor.to(d.channelUri)

        //in case of on[Exception] at RouteBuilder level, the processor.to is managed by the Handler.parseOnException
        case _ =>
      }

      //beginning of subroute
      step.buildHelper.from(d.channelUri).routeId(d.routeId)
    }

  }

  private[this] def endpointImplementation: Process = {
    case StepInformation(endpoint @ EndpointDefinition(CamelSink(uri), requestReply), camelProcessorDefinition: ProcessorDefinition[_]) => {
      requestReply.foreach(x => camelProcessorDefinition.setExchangePattern(if (x) ExchangePattern.InOut else ExchangePattern.InOnly))
      camelProcessorDefinition.to(uri)
    }
  }

  private[this] def multicast: Process = {
    case StepInformation(MulticastDefinition(SeqCamelSink(sinks @ _*)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val uris = sinks.map(_.uri)
      val javaUris = uris.toArray
      camelProcessorDefinition.multicast().to(javaUris: _*)

    }
  }

  private[this] def bodyConvertor: Process = {
    case StepInformation(BodyConvertorDefinition(inClass, outClass), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.convertBodyTo(outClass)
    }
  }

  private[this] def bodyTypeValidation: Process = {
    case StepInformation(BodyTypeValidationDefinition(inClass, outClass), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(new CamelBodyTypeValidation(outClass))
    }
  }

  /**
    * Parses the "choice" keyword, which implies to process both the when statement and the otherwise statement.
    * @see CamelDSL.StepImplementation
    */
  private[this] def choice: Process = {
    case s @ StepInformation(d: ChoiceDefinition[_], camelProcessorDefinition: ProcessorDefinition[_]) => {

      implicit val implicitRouteBuilder = s.buildHelper

      // declare a choice in camel
      val camelChoice = camelProcessorDefinition.choice()

      // for each possible branch of the choice create a camel predicate and declare a when in camel
      for (when <- d.scopedSteps) {
        val camelWhen = camelChoice.when(Predicates.toCamelPredicate(when.predicate))
        for (step <- when.next) {
          process(step, camelWhen)(s.buildHelper)
        }
      }

      // if an otherwise branch exists, declare an otherwise subroute in camel
      for { otherwise <- d.otherwise; step <- otherwise.next } {
        val camelOtherwise = camelChoice.otherwise()
        process(step, camelOtherwise)
      }

      // end() declares the end of the choice block in camel.
      // Don't use endchoice() ! It is only used when ending a sub route (loadBalance, split, etc...) in the choice block

      // end() is also a difficulty to make the creation of the camel route tailrec because end() needs to be called after the subroutes are created.
      camelChoice.end()

    }
  }

  /**
    * Parses the split keyword and implements a camel splitter from a babel splitter definition.
    */
  private[this] def splitter: Process = {

    case StepInformation(SplitterDefinition(expression), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.split(Expressions.toJavaIteratorCamelExpression(expression))

    }
  }

  /**
    * Parses the filter keyword and implements a camel filter from a babel filter definition.
    */
  private[this] def filter: Process = {

    case StepInformation(FilterDefinition(predicate), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val nextProcDef = camelProcessorDefinition.filter(Predicates.toCamelPredicate(predicate))

      nextProcDef
    }
  }

  /**
    * parses the error handling keyword, at both level : Route and RouteBuilder
    */
  private[this] def handler: Process = {

    case step @ StepInformation(s: HandlerDefinition, camelProcessorDefinition) => {
      s.scopedSteps.foreach(x => process(x, camelProcessorDefinition)(step.buildHelper))
      camelProcessorDefinition
    }

  }
}

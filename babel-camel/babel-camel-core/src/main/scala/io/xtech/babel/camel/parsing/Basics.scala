/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.CamelDSL
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.model._
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.ExchangePattern
import org.apache.camel.model.{ ProcessorDefinition, SplitDefinition }

import scala.collection.immutable
import scala.language.implicitConversions

/**
  * Basics is the main parsing trait. It contains the main (or most basic) keywords parsing.
  * It is enriched by the other CamelParsing traits to provides to the CamelDSL all the possible `steps`.
  */
private[babel] trait Basics extends CamelParsing { self: CamelDSL =>

  protected def steps: immutable.Seq[Process] = immutable.Seq(from,
    handle,
    handler,
    endpointImplementation,
    bodyConvertor,
    choice,
    splitter,
    filter,
    multicast,
    bodyTypeValidation)

  private[this] def from: Process = {
    case s @ StepInformation(step @ FromDefinition(_, source @ CamelSource(uri)), _) => {
      val from = s.buildHelper.from(uri)
      namingStrategy.newRoute()
      namingStrategy.name(step).foreach(from.id) //might be renamed when given a routeId
      from
    }
  }

  private[this] def handle: Process = {
    case s @ StepInformation(_: EmptyDefinition, camelProcessor) => {

      s.buildHelper
    }
  }

  private[this] def endpointImplementation: Process = {
    case StepInformation(step @ EndpointDefinition(CamelSink(uri), requestReply), camelProcessorDefinition: ProcessorDefinition[_]) => {
      requestReply.foreach(x => camelProcessorDefinition.setExchangePattern(if (x) ExchangePattern.InOut else ExchangePattern.InOnly))
      camelProcessorDefinition.to(uri).withId(step)
    }
  }

  private[this] def multicast: Process = {
    case StepInformation(step @ MulticastDefinition(SeqCamelSink(sinks @ _*)), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val uris = sinks.map(_.uri)
      val javaUris = uris.toArray
      camelProcessorDefinition.multicast().to(javaUris: _*).withId(step)

    }
  }

  private[this] def bodyConvertor: Process = {
    case StepInformation(step @ BodyConvertorDefinition(inClass, outClass), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.convertBodyTo(outClass).withId(step)
    }
  }

  private[this] def bodyTypeValidation: Process = {
    case StepInformation(step @ BodyTypeValidationDefinition(inClass, outClass), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.process(new CamelBodyTypeValidation(outClass)).withId(step)
    }
  }

  /**
    * Parses the "choice" keyword, which implies to process both the when statement and the otherwise statement.
    * @see CamelDSL.StepImplementation
    */
  private[this] def choice: Process = {
    case s @ StepInformation(step: ChoiceDefinition[_], camelProcessorDefinition: ProcessorDefinition[_]) => {

      implicit val implicitRouteBuilder = s.buildHelper

      // declare a choice in camel
      val camelChoice = camelProcessorDefinition.choice()
      camelChoice.withId(step) //if directly done on camelChoice definition, would require casting.

      // for each possible branch of the choice create a camel predicate and declare a when in camel
      for (when <- step.scopedSteps) {
        val camelWhen = camelChoice.when(Predicates.toCamelPredicate(when.predicate)).withId(when)
        for (step <- when.next) {
          process(step, camelWhen)(s.buildHelper)
        }
      }

      // if an otherwise branch exists, declare an otherwise subroute in camel
      for { otherwise <- step.otherwise; step <- otherwise.next } {
        val camelOtherwise = camelChoice.otherwise()
        //unfortunately, camel overrides otherwise id with choice id.
        //thus, no id is generated for otherwise
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

    case StepInformation(step @ SplitterDefinition(expression, stop, propagate), camelProcessorDefinition: ProcessorDefinition[_]) => {

      val definition = camelProcessorDefinition.split(Expressions.toJavaIteratorCamelExpression(expression))
      splitParameters(definition, step).withId(step)

    }

    case s @ StepInformation(step: SplitReduceDefinition[_, _, _], camelProcessorDefinition: ProcessorDefinition[_]) => {

      val aggregationStrategy = new ReduceBodyAggregationStrategy(step.reduce)
      val split = camelProcessorDefinition.split(Expressions.toJavaIteratorCamelExpression(step.expression), aggregationStrategy)
      splitParameters(split, step)
      step.internalRouteDefinition.next.foreach(splitterRoute => process(splitterRoute, split)(s.buildHelper))

      split.end().withId(step)
    }

    case s @ StepInformation(step: SplitFoldDefinition[_, _, _, _], camelProcessorDefinition: ProcessorDefinition[_]) => {

      val aggregationStrategy = new FoldBodyAggregationStrategy(step.seed, step.fold)
      val split = camelProcessorDefinition.split(Expressions.toJavaIteratorCamelExpression(step.expression), aggregationStrategy)
      splitParameters(split, step)
      step.internalRouteDefinition.next.foreach(splitterRoute => process(splitterRoute, split)(s.buildHelper))

      split.end().withId(step)
    }

  }

  private def splitParameters(splitCamel: SplitDefinition, splitBabel: SplitConfiguration): SplitDefinition = {
    if (splitBabel.stopOnException) {
      splitCamel.stopOnException()
    }
    if (splitBabel.propagateException) {
      splitCamel.shareUnitOfWork()
    }
    splitCamel
  }

  /**
    * Parses the filter keyword and implements a camel filter from a babel filter definition.
    */
  private[this] def filter: Process = {

    case StepInformation(step @ FilterDefinition(predicate), camelProcessorDefinition: ProcessorDefinition[_]) => {

      camelProcessorDefinition.filter(Predicates.toCamelPredicate(predicate)).withId(step)
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

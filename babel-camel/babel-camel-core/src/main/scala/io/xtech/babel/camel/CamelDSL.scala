/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.camel.parsing.{ Aggregation, _ }
import io.xtech.babel.fish.model._
import io.xtech.babel.fish.parsing.{ StepInformation, StepProcessor }
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.ModelCamelContext
import scala.collection.immutable

// TODO will be fixed in 2.12 https://issues.scala-lang.org/browse/SI-6541

import scala.language.{ existentials, implicitConversions }

/**
  * Babel Camel is the way to create a Camel route with the Babel fish DSL.
  *
  * '''CamelDSL usage example :'''
  * {{{
  * import io.xtech.babel.camel.builder.RouteBuilder
  *
  * val routeDef = new RouteBuilder {
  *   from("direct:input").to("mock:output")
  * }
  *
  * }}}
  */
trait CamelDSL extends StepProcessor[RouteBuilder] with Basics
    with Aggregation
    with Transaction
    with Transformation
    with Marshaller
    with RouteId
    with Enricher
    with Log
    with Sort
    with Resequencer
    with Handler
    with RecipientList
    with RouteConfiguration
    with WireTap
    with Validation {

  implicit def stringSource(uri: String): CamelSource = CamelSource(uri)

  implicit def stringSink(uri: String): CamelSink[Any] = CamelSink(uri)

  implicit def stringSinks(uris: Seq[String]): immutable.Seq[CamelSink[Any]] = immutable.Seq(uris.map(new CamelSink(_)): _*)

  //transforms a Camel Expression to a Babel Expression, but does not apply on instances which inherit of the Camel Expression
  //otherwise, scala implicits would not be able to differ camelPredicateExpression and camelPredicate
  // on a Camel Predicate which extends Camel Expression
  implicit def camelPredicateExpression(exp: org.apache.camel.Expression with org.apache.camel.Predicate) = new CamelExpressionWrapper(exp)
  implicit def camelPredicate(pred: org.apache.camel.Predicate) = new CamelPredicateWrapper(pred)

  protected[camel] def process[T](step: StepDefinition, previous: T)(implicit routeBuilder: RouteBuilder) = {
    processSteps(StepInformation(step, previous)(routeBuilder))
  }

  /**
    * Creates a Camel RouteBuilder from DSL Definition. The RouteBuilder can be used to declare the routes in a CamelContext.
    * @param routeDefinitions  A Seq of route definition builded by the Babel fish DSL.
    * @return a new camel route builder.
    */
  protected[camel] def routeBuilder(routeDefinitions: immutable.Seq[RouteDefinition])(implicit camelContext: ModelCamelContext): RouteBuilder = {
    val routeBuilder = new RouteBuilder() {
      def configure(): Unit = {
        routeDefinitions.foreach(routeDefinition => processSteps(StepInformation(routeDefinition.from, None)(this)))
      }
    }
    routeBuilder.setContext(camelContext)
    routeBuilder
  }

  implicit protected def camelMessage[I](msg: Message[I]): CamelMessage[I] = msg match {
    case m: CamelMessage[I] => m
    case other              => throw new IllegalArgumentException(s"Messages in Babel Camel should always be CamelMessage, but was $other")
  }

}

object CamelException {

  /**
    * An exception thrown when a when is not known in the fish.
    */
  class UnknownWhenProps extends Exception("An unknown when strategy was given")

  class ErorrHandlingDefinedTwice extends Exception("The handle may be used only once in the RouteBuilder")

}
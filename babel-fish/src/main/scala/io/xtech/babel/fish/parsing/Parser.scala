/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.parsing

import io.xtech.babel.fish.model.StepDefinition
import scala.annotation.tailrec
import scala.collection.immutable

case class StepInformation[B](step: StepDefinition, previousStepHelper: Any)(implicit val buildHelper: B)

/**
  * base trait for Parsing objects.
  * Enforces parsing objects to define one sequence of each possible parsing definition.
  * @tparam T
  */
trait Parsing[T] {
  type Process = PartialFunction[StepInformation[T], Any]

  //any for last reduced
  def steps: immutable.Seq[Process]
}

/**
  * base trait for DSL parsers (CamelDSL and SpringIntegrationDSL).
  * Provides a method which recursively parses an EIP definition and its children.
  * @tparam B
  */

trait StepProcessor[B] extends Parsing[B] {

  private[this] lazy val parse: Function1[StepInformation[B], Option[Any]] = steps.reduce(_ orElse _).lift

  /**
    * Processes each step recursively (but the from, managed by the process method.
    * Uses the aggregated parsing method which, for each possible keyword, create the required Mediation engine instance.
    * @param info containing the informations of the current step.
    * @return the info updated with the next step if any, otherwise the info as processed, to get its RouteBuilder.
    * @see CamelDSL.process
    */

  @tailrec
  protected[babel] final def processSteps(info: StepInformation[B]): Unit = {
    val processed = parse(info).getOrElse(throw new ParsingException.UnknownStepException(info.step.toString)) //todo add step.toString for better comprehension)
    info.step.next match {
      case Some(next) => {
        processSteps(StepInformation(next, processed)(info.buildHelper))
      }
      case s =>
    }
  }

}


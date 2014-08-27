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

package io.xtech.babel.fish.model

import scala.collection.immutable

/*
 * Definitions objects that define the "choice" in the DSL
 */

/**
  * The definition of the "choice" block in the DSL.
  */
case class ChoiceDefinition[I]() extends ScopeDefinition[WhenDefinition[I]] {

  /**
    * A helper method for non-scala language.
    * It adds a when definition to this choice definition.
    * @param when a When defintion.
    */
  def addWhen(when: WhenDefinition[I]): Unit = {
    this.scopedSteps = scopedSteps :+ when
  }

  /**
    * @see WhenDefinition
    * @return a list of subroutes
    */
  var otherwise: Option[OtherwiseDefinition] = None

  override def validate(): immutable.Seq[ValidationError] = {

    // validate the subroutes
    val whenErrors = scopedSteps.map(_.validate()).flatten
    val otherwiseErrors = otherwise.map(_.validate()).getOrElse(immutable.Seq.empty)

    // validate the children definitions
    val errors = super.validate()

    // return all the errors
    whenErrors ++ otherwiseErrors ++ errors
  }
}

/**
  * This class defines a subroute with a predicate.
  * @param predicate an object that defined the predicate.
  * @tparam I body type of the message.
  */
class WhenDefinition[I](val predicate: Predicate[I]) extends StepDefinition

/**
  * This class defines a subroute if no other subroutes handle the message.
  */
class OtherwiseDefinition extends StepDefinition
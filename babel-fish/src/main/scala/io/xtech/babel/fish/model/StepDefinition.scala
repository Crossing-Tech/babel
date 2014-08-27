/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish.model

import scala.collection.immutable

/**
  * A StepDefinition is the base for all Definition in the DSL.
  * It define only the next node in the DSL.
  */
trait StepDefinition {

  /**
    * the next node in the fish.
    */
  var next: Option[StepDefinition] = None

  /**
    * meta information such as uuid, description.
    * potentially provided by other systems.
    */
  var metaInformation: immutable.Map[String, Any] = immutable.Map.empty[String, Any]

  /**
    * Validates the definition after the declaration in the fish.
    * @return a list of errors. Each error contains an error message and the invalid definition.
    */
  def validate(): immutable.Seq[ValidationError] = {

    var errors = immutable.Seq.empty[ValidationError]

    // commons validation
    val endpointError: immutable.Seq[ValidationError] = (this, next) match {
      // a from definition should not end a route
      case (FromDefinition(_, _), None)           => immutable.Seq(ValidationError("a route or subroute may not end using a from statement", Some(this)))
      //a from definition should not follow another definition
      case (_, Some(from @ FromDefinition(_, _))) => immutable.Seq(ValidationError("a from may only start a route", Some(from)))
      // otherwise,  everything is currently valid
      case (_, _)                                 => immutable.Seq.empty
    }

    errors = endpointError ++ errors

    // children validation
    val childrenErrors = next.map(_.validate()).getOrElse(immutable.Seq.empty)

    errors = childrenErrors ++ errors

    errors
  }
}

/**
  * A ValidationError is an error found during the validation phase of the DSL.
  * @param errorMessage the error message
  * @param definition the definition with an error
  */
case class ValidationError(errorMessage: String, definition: Option[StepDefinition] = None)

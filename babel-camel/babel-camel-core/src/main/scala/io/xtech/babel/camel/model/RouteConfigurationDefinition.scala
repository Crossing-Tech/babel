/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.StepDefinition
import org.apache.camel.Route

/*
 * Route configuration model
 */

case class AutoStartDefinition(noAutoStart: Boolean) extends StepDefinition

case class OnExchangeBeginDefinition[I](callback: (Route, CamelMessage[I]) => Unit) extends StepDefinition

case class OnExchangeDoneDefinition[I](callback: (Route, CamelMessage[I]) => Unit) extends StepDefinition

case class OnStopDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnResumeDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnInitDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnRemoveDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnStartDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnSuspendDefinition(callback: (Route) => Unit) extends StepDefinition


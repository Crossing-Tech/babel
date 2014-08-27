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

package io.xtech.babel.camel.model

import io.xtech.babel.fish.model.StepDefinition
import org.apache.camel.Route

/*
 * Route configuration model
 */

case class NoAutoStartDefinition() extends StepDefinition

case class OnExchangeBeginDefinition[I](callback: (Route, CamelMessage[I]) => Unit) extends StepDefinition

case class OnExchangeDoneDefinition[I](callback: (Route, CamelMessage[I]) => Unit) extends StepDefinition

case class OnStopDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnResumeDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnInitDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnRemoveDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnStartDefinition(callback: (Route) => Unit) extends StepDefinition

case class OnSuspendDefinition(callback: (Route) => Unit) extends StepDefinition


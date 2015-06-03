/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.model._
import io.xtech.babel.fish.{ BaseDSL2FromDSL, FromDSL }
import org.apache.camel.Route

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Adds configuration keywords to the FromDSL (start of the route).
  */
private[camel] class RouteConfigurationDSL[I: ClassTag](protected val baseDsl: FromDSL[I]) extends BaseDSL2FromDSL[I] {

  /**
    * The noAutoStartup avoid the current route to be started at the same time as the Camel Context
    * @return the possibility to add other steps to the current DSL
    */
  def noAutoStartup: FromDSL[I] = {

    NoAutoStartDefinition()

  }

  /**
    * The oneExchangeBegin let you add a callback function which will be called each time the consumer issue an exchange.
    * @param callback function called on exchange creation
    * @return the possibility to add other steps to the current DSL
    */
  def onExchangeBegin(callback: (Route, CamelMessage[I]) => Unit): FromDSL[I] = {

    OnExchangeBeginDefinition(callback)

  }

  /**
    * The oneExchangeBegin let you add a callback function which will be called each time exchange ends its flow.
    * @param callback function called on exchange end
    * @return the possibility to add other steps to the current DSL
    */
  def onExchangeDone(callback: (Route, CamelMessage[I]) => Unit): FromDSL[I] = {

    OnExchangeDoneDefinition(callback)

  }

  /**
    * The onStop lets you add a callback function which will be called each time the route is stopped.
    * @param callback called each time the route is stopped.
    * @return the possibility to add other steps to the current DSL
    */
  def onStop(callback: (Route) => Unit): FromDSL[I] = {

    OnStopDefinition(callback)

  }

  /**
    * The onResume let you add a callback function which will be called each time the route is resumed.
    * @param callback called each time the route is resumed.
    * @return the possibility to add other steps to the current DSL
    */
  def onResume(callback: (Route) => Unit): FromDSL[I] = {

    OnResumeDefinition(callback)

  }

  /**
    * The onInit let you add a callback function which will be called each time the route is initialized.
    * @param callback called each time the route is initialized.
    * @return the possibility to add other steps to the current DSL
    */
  def onInit(callback: (Route) => Unit): FromDSL[I] = {

    OnInitDefinition(callback)

  }

  /**
    * The onRemove lets you add a callback function which will be called each time the route is removed.
    * @param callback called each time the route is removed.
    * @return the possibility to add other steps to the current DSL
    */
  def onRemove(callback: (Route) => Unit): FromDSL[I] = {

    OnRemoveDefinition(callback)

  }

  /**
    * The onStart lets you add a callback function which will be called each time the route is started.
    * @param callback called each time the route is started.
    * @return the possibility to add other steps to the current DSL
    */
  def onStart(callback: (Route) => Unit): FromDSL[I] = {

    OnStartDefinition(callback)

  }

  /**
    * The onSuspend lets you add a callback function which will be called each time the route is suspended.
    * @param callback called each time the route is suspended.
    * @return the possibility to add other steps to the current DSL
    */
  def onSuspend(callback: (Route) => Unit): FromDSL[I] = {

    OnSuspendDefinition(callback)

  }
}

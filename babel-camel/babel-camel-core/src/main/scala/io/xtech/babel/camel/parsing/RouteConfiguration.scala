/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.parsing

import io.xtech.babel.camel.RouteConfigurationDSL
import io.xtech.babel.camel.model._
import io.xtech.babel.fish.FromDSL
import io.xtech.babel.fish.parsing.StepInformation
import org.apache.camel.model.RouteDefinition
import org.apache.camel.spi.RoutePolicy
import org.apache.camel.{ Exchange, Route }

import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The route configuration parser.
  */
private[babel] trait RouteConfiguration extends CamelParsing {

  abstract override def steps: immutable.Seq[Process] = super.steps :+ parse

  implicit def routeConfigurationDSLExtension[I: ClassTag](baseDsl: FromDSL[I]) = new RouteConfigurationDSL[I](baseDsl)

  private[this] def parse: Process = {

    case StepInformation(NoAutoStartDefinition(), camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.noAutoStartup()
      camelProcessorDefinition

    case StepInformation(d: OnExchangeBeginDefinition[_], camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onExchangeBegin(route: Route, exchange: Exchange): Unit = {
          d.asInstanceOf[OnExchangeBeginDefinition[Any]].callback(route, new CamelMessage[Any](exchange.getIn))
        }
      })

      camelProcessorDefinition

    case StepInformation(d: OnExchangeDoneDefinition[_], camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onExchangeDone(route: Route, exchange: Exchange): Unit = {
          d.asInstanceOf[OnExchangeDoneDefinition[Any]].callback(route, new CamelMessage[Any](exchange.getIn))
        }
      })

      camelProcessorDefinition

    case StepInformation(d: OnStopDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onStop(route: Route): Unit = d.callback(route)
      })

      camelProcessorDefinition

    case StepInformation(d: OnResumeDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onResume(route: Route): Unit = d.callback(route)
      })

      camelProcessorDefinition

    case StepInformation(d: OnInitDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onInit(route: Route): Unit = d.callback(route)
      })

      camelProcessorDefinition

    case StepInformation(d: OnRemoveDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onRemove(route: Route): Unit = d.callback(route)
      })

      camelProcessorDefinition

    case StepInformation(d: OnStartDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {
        override def onStart(route: Route): Unit = d.callback(route)
      })

      camelProcessorDefinition

    case StepInformation(d: OnSuspendDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface {

        override def onSuspend(route: Route): Unit = {
          d.callback(route)
        }
      })

      camelProcessorDefinition

  }

}

trait RoutePolicyInterface extends RoutePolicy {
  def onExchangeDone(route: Route, exchange: Exchange): Unit = {}

  def onStop(route: Route): Unit = {}

  def onExchangeBegin(route: Route, exchange: Exchange): Unit = {}

  def onResume(route: Route): Unit = {}

  def onInit(route: Route): Unit = {}

  def onRemove(route: Route): Unit = {}

  def onStart(route: Route): Unit = {}

  def onSuspend(route: Route): Unit = {}
}
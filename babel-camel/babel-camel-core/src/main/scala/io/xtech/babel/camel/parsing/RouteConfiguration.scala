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
import org.apache.camel.model.{ ProcessorDefinition, RouteDefinition }
import org.apache.camel.spi.RoutePolicy
import org.apache.camel.{ Exchange, Route }

import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * The route configuration parser.
  */
private[babel] trait RouteConfiguration extends CamelParsing {

  abstract override protected def steps: immutable.Seq[Process] = super.steps :+ parse

  private[this] def parse: Process = {

    case StepInformation(AutoStartDefinition(autoStart), camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.autoStartup(autoStart)

    case StepInformation(d: OnExchangeBeginDefinition[_], camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(oeb = Some(d)))

    case StepInformation(d: OnExchangeDoneDefinition[_], camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(oed = Some(d)))

    case StepInformation(d: OnStopDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(os = Some(d)))

    case StepInformation(d: OnResumeDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(or = Some(d)))

    case StepInformation(d: OnInitDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(oi = Some(d)))

    case StepInformation(d: OnRemoveDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(orm = Some(d)))

    case StepInformation(d: OnStartDefinition, camelProcessorDefinition: RouteDefinition) =>

      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(ost = Some(d)))

    case StepInformation(d: OnSuspendDefinition, camelProcessorDefinition: RouteDefinition) =>
      camelProcessorDefinition.routePolicy(new RoutePolicyInterface(osu = Some(d)))

    case s @ StepInformation(policies: RoutePolicyDefinition, camelProcessorDefinition: ProcessorDefinition[_]) =>
      s.buildHelper.getRouteCollection.getRoutes.asScala.lastOption.map(_.routePolicy(policies.policy: _*))
      camelProcessorDefinition

  }

  protected implicit def routeConfigurationDSLExtension[I: ClassTag](baseDsl: FromDSL[I]) = new RouteConfigurationDSL[I](baseDsl)

}

class RoutePolicyInterface(oed: Option[OnExchangeDoneDefinition[_]] = None,
                           oeb: Option[OnExchangeBeginDefinition[_]] = None,
                           os: Option[OnStopDefinition] = None,
                           or: Option[OnResumeDefinition] = None,
                           oi: Option[OnInitDefinition] = None,
                           orm: Option[OnRemoveDefinition] = None,
                           ost: Option[OnStartDefinition] = None,
                           osu: Option[OnSuspendDefinition] = None) extends RoutePolicy {

  private implicit def unlift[I](exchange: Exchange): CamelMessage[I] = {
    new CamelMessage[I](exchange.getIn)
  }

  def onExchangeDone(route: Route, exchange: Exchange): Unit = oed.fold({})(_.callback(route, exchange))

  def onExchangeBegin(route: Route, exchange: Exchange): Unit = oeb.fold({})(_.callback(route, exchange))

  def onResume(route: Route): Unit = or.fold({})(_.callback(route))

  def onInit(route: Route): Unit = oi.fold({})(_.callback(route))

  def onRemove(route: Route): Unit = orm.fold({})(_.callback(route))

  def onStop(route: Route): Unit = os.fold({})(_.callback(route))

  def onStart(route: Route): Unit = ost.fold({})(_.callback(route))

  def onSuspend(route: Route): Unit = osu.fold({})(_.callback(route))
}
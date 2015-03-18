/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel.builder

import io.xtech.babel.camel._
import io.xtech.babel.camel.model.EmptyDefinition
import io.xtech.babel.fish.model.RouteDefinition
import io.xtech.babel.fish.{ DSL, NoDSL }
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.{ CamelContext, RoutesBuilder, RuntimeCamelException }

/**
  * Facility trait used to implement RouteBuilder in order to be more integrated into Camel.
  * This allows Babel Camel routes to be discovered by the Camel route scanner.
  */
abstract class RouteBuilder extends DSL with CamelDSL with RoutesBuilder {

  /**
    * stores the RouteBuilder scope configuration
    */
  private[this] var handle: Option[EmptyDefinition] = None

  /**
    * defines a set of rules that are applied on the RouteBuilder itself and not on a route.
    * @param block is the rules to be applied to the RouteBuilder
    * @return end of DSL: it is not possible to add other keywords to the current DSL.
    */
  protected def handle(block: HandlingDSL[Any] => Unit): NoDSL = {
    val dsl = new GlobalDSL()
    handle match {
      case None =>
        handle = Some(dsl.step)
      case Some(h) =>
        throw new CamelException.ErorrHandlingDefinedTwice
    }
    new HandlerDSL[Any](dsl).handle(block)
  }

  /**
    * adds the generated routes to the given Camel Context.
    * Used by Spring to populate a Camel Context while scanning the package (though xml configuration)
    * @param context to be populated with generated routes
    */

  def addRoutesToCamelContext(context: CamelContext): Unit = context match {
    case model: ModelCamelContext =>
      val definitions = handle.map(new RouteDefinition(_)).toList ::: this.build().toList
      val routeBuilder = this.routeBuilder(definitions)(model)
      model.addRoutes(routeBuilder)

    case other => throw new RuntimeCamelException("Requires a ModelCamelContext in order to add Babel Fish routes to it.")
  }

}

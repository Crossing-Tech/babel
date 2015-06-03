/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.NamingStrategy
import io.xtech.babel.fish.model.StepDefinition
import org.specs2.mutable._

import scala.collection.JavaConverters._

class IdSpec extends SpecificationWithJUnit {
   sequential

   "A id" should {

     "be set to a from (using routeId)" in new camel {

       import io.xtech.babel.camel.builder.RouteBuilder

       val routeId = "route2"
       val routeBuilder = new RouteBuilder {
         //#doc:babel-camel-id-from
         from("direct:input").
           //the id may not be configured for the from
           routeId(routeId).
           to("mock:output")
       }
       //#doc:babel-camel-id-from
       val otherRoute = new org.apache.camel.builder.RouteBuilder() {
         def configure(): Unit = {
           from("direct:camel1").id("camelFromId1").routeId("camelRouteId1").to("mock:camel") //routeId = camelRouteId1
           from("direct:camel2").routeId("camelRouteId2").id("camelFromId2").to("mock:camel") //routeId = camelFromId2
           from("direct:camel3").id("camelFromId3").to("mock:camel") // routeId = camelFromId3
         }
       }

       camelContext.addRoutes(otherRoute)
       camelContext.addRoutes(routeBuilder)

       camelContext.start()

       //checks camel behavior with routeId and id for from:
       val routeIds = camelContext.getRouteDefinitions.asScala.map(_.getId)
       routeIds must contain(routeId)
       routeIds must contain("camelRouteId1")
       routeIds must contain("camelFromId2")
       routeIds must contain("camelFromId3")

     }

     "be set to other eip" in new camel {

       import io.xtech.babel.camel.builder.RouteBuilder

       val routeId = "route3"
       //#doc:babel-camel-id-eip
       val routeBuilder = new RouteBuilder {
         from("direct:input").
           routeId(routeId).
           //the id of the processor will be "myProcess"
           processBody(x => x).id("myProcess").
           //the id of the mock endpoint will be "mock"
           to("mock:babel").id("mock")
       }
       //#doc:babel-camel-id-eip

       camelContext.addRoutes(routeBuilder)

       camelContext.start()

       Option(camelContext.getRouteDefinition(routeId)).map(_.getOutputs.get(0).getId) must be_==(Some("myProcess"))
       Option(camelContext.getRouteDefinition(routeId)).map(_.getOutputs.get(1).getId) must be_==(Some("mock"))
     }

     "allows default ids" in new camel {

       import io.xtech.babel.camel.builder.RouteBuilder

       val routeId = "route4"
       //#doc:babel-camel-id-eip
       val routeBuilder = new RouteBuilder {

         override protected implicit def namingStrategy: NamingStrategy = new NamingStrategy {
           var index = 0
         override def name(stepDefinition: StepDefinition): Option[String] = {
           index += 1
           Some(s"id-$index")
         }
         override protected[babel] def newRoute(): Unit = {index = 0}
         }
         from("direct:input").
           routeId(routeId).
           sub("subroute").
           //the id of the processor will be "myProcess"
           processBody(x => x).
           //the id of the mock endpoint will be "mock"
           to("mock:babel-sub")
       }
       //#doc:babel-camel-id-eip

       camelContext.addRoutes(routeBuilder)

       camelContext.start()

       Option(camelContext.getRouteDefinition("subroute")).map(_.getOutputs.get(0).getId).get must contain("subroute_pro_3_")
       Option(camelContext.getRouteDefinition("subroute")).map(_.getOutputs.get(1).getId).get must be_==("subroute_end_4_mock:babel-sub")
     }

   }

 }

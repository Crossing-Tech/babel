/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.test.camel
import io.xtech.babel.fish.NamingStrategy
import io.xtech.babel.fish.model.StepDefinition
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.{ Exchange, Processor }
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

      //#doc:babel-camel-id-default
      val routeBuilder = new RouteBuilder {

        override protected implicit val namingStrategy: NamingStrategy = new NamingStrategy {
          var index = 0
          override def name(stepDefinition: StepDefinition): Option[String] = {
            index += 1
            Some(s"id-$index")
          }
          override protected[babel] def newRoute(): Unit = {}
        }

        //the id of the from (and thus the routeId) will be "id-1"
        from("direct:input").
          //the id of the endpoint which allows the subroute will be "id-2"
          processBody(x => x).
          //the id of the mock endpoint will be "id-4"
          to("mock:babel-sub")
      }
      //#doc:babel-camel-id-default

      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      Option(camelContext.getRouteDefinition("id-1")).map(_.getOutputs.get(0).getId) must be_==(Some("id-2"))
      Option(camelContext.getRouteDefinition("id-1")).map(_.getOutputs.get(0).getId) must be_==(Some("id-3"))
    }

    "allows default ids depending on patterns" in new camel {
      //#doc:babel-camel-id-strategy

      import io.xtech.babel.fish.model.StepDefinition
      import io.xtech.babel.fish.NamingStrategy
      import io.xtech.babel.camel.model.{ LogMessage, LogDefinition }
      val routeDef = new RouteBuilder {

        override protected implicit val namingStrategy = new NamingStrategy {
          override def name(stepDefinition: StepDefinition): Option[String] = stepDefinition match {
            //set the id of endpoints to their uri
            case LogDefinition(LogMessage(message)) => Some(s"log:$message")
            //do not modify other EIP ids
            case other                              => None
          }

          override def newRoute(): Unit = {}
        }

        from("direct:input").routeId("babel")
          .process(msg => msg.withBody(_ + "bli"))
          //the id of log EIP will be "log:body ${body}"
          .log("body ${body}")
          //the other pattern id will not be changed by babel
          .to("mock:output")
      }
      //#doc:babel-camel-id-strategy

      val route = new org.apache.camel.builder.RouteBuilder() {

        override def configure(): Unit = {
          from("direct:camel").routeId("camel")
            .process(new Processor {
              override def process(p1: Exchange): Unit = {
                println("toto")
              }
            }).id("toto-camel")
            .to("mock:camel")
        }
      }

      routeDef.addRoutesToCamelContext(camelContext)
      camelContext.addRoutes(route)

      camelContext.asInstanceOf[ModelCamelContext].getManagementStrategy.setManagementNamingStrategy(new MyNames())

      camelContext.start()

      val producer = camelContext.createProducerTemplate()

      val mockEnpoint = camelContext.getEndpoint("mock:output").asInstanceOf[MockEndpoint]
      mockEnpoint.expectedBodiesReceived("blablibli")

      producer.sendBody("direct:input", "blabli")

      camelContext.getRouteDefinition("camel").getOutputs.get(0).getId === "toto-camel"
      camelContext.getRouteDefinition("babel").getOutputs.get(1).getId === "log:body ${body}"

      mockEnpoint.assertIsSatisfied()
    }

  }

}

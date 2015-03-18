/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.camel

import io.xtech.babel.camel.builder.RouteBuilder
import io.xtech.babel.camel.parsing.RoutePolicyInterface
import io.xtech.babel.camel.test.camel
import org.apache.camel.support.ServiceSupport
import org.apache.camel.{ Exchange, Route }
import org.specs2.mutable.SpecificationWithJUnit

class RouteConfigurationSpec extends SpecificationWithJUnit {
  sequential

  "A route configuration" should {

    "manage noAutoStartup" in new camel {

      import io.xtech.babel.camel.builder.RouteBuilder

      //#doc:babel-camel-route-conf-1

      val routeBuilder = new RouteBuilder {
        from("direct:input").routeId("babel").
          //The route is told not starting with the Camel Context
          //   but wait until beeing started especially.
          noAutoStartup.
          to("mock:output")
      }
      //#doc:babel-camel-route-conf-1

      val camelRoute = new org.apache.camel.builder.RouteBuilder() {
        def configure(): Unit = {
          from("direct:inputCamel").routeId("camel").noAutoStartup().
            to("mock:output")
        }
      }
      camelContext.addRoutes(camelRoute)
      camelContext.addRoutes(routeBuilder)

      camelContext.start()

      camelContext.getRoute("camel").asInstanceOf[ServiceSupport].isStarted === false
      camelContext.getRoute("babel").asInstanceOf[ServiceSupport].isStarted === false
    }

    "manage routePolicy " in {
      "onStop" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").onStop(route => success = true).to("mock:output")
        }

        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onStop(route: Route) {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()
        camelContext.stop

        csuccess must beTrue
        success must beTrue
      }

      "onStart" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").onStart(route => success = true).to("mock:output")
        }

        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onStart(route: Route): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()

        csuccess must beTrue
        success must beTrue
      }

      "onInit" in new camel {
        var csuccess: Boolean = false
        //#doc:babel-camel-route-conf-2
        var success: Boolean = false
        val routeBuilder = new RouteBuilder {
          from("direct:input").
            //As the route is initialing, the success variable is set to true
            onInit(route => success = true).
            to("mock:output")
        }
        //#doc:babel-camel-route-conf-2
        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onInit(route: Route): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()

        csuccess must beTrue
        success must beTrue
      }

      "onSuspend" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").routeId("toto").onSuspend(route => {
            println("suspend!!!")
            success = true
          }).to("mock:output")
        }
        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onSuspend(route: Route): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start
        camelContext.suspend

        csuccess must beTrue
        success must beTrue
      }

      "onResume" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").routeId("toto").onResume(route => success = true).to("mock:output")
        }

        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onResume(route: Route): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()
        camelContext.suspend()

        success must beFalse

        camelContext.resume()

        csuccess must beTrue
        success must beTrue
      }

      "onRemove" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").routeId("toto").onRemove(route => success = true).to("mock:output")
        }
        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onRemove(route: Route): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()
        camelContext.stop()
        camelContext.removeRoute("toto")

        csuccess must beTrue
        success must beTrue
      }

      "onExchangeBegin" in new camel {
        var success: Boolean = false
        var csuccess: Boolean = false

        val routeBuilder = new RouteBuilder {
          from("direct:input").onExchangeBegin((route, message) => success = true).to("mock:output")
        }
        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onExchangeBegin(route: Route, exchange: Exchange): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()
        camelContext.createProducerTemplate().sendBody("direct:inputCamel", "toto")
        camelContext.createProducerTemplate().sendBody("direct:input", "toto")

        csuccess must beTrue
        success must beTrue
      }

      "onExchangeDone" in new camel {
        var csuccess: Boolean = false

        //#doc:babel-camel-route-conf-3
        var success: Boolean = false
        val routeBuilder = new RouteBuilder {
          from("direct:input").
            //At each time an exchange reach the end of the route,
            //   the success variable is set to true
            onExchangeDone((exchange, route) => success = true).
            to("mock:output")
        }
        //#doc:babel-camel-route-conf-3
        val camelRoute = new org.apache.camel.builder.RouteBuilder() {
          def configure(): Unit = {
            from("direct:inputCamel").routeId("camel").routePolicy(new RoutePolicyInterface {
              override def onExchangeDone(route: Route, exchange: Exchange): Unit = {
                csuccess = true
              }
            }).
              to("mock:output")
          }
        }
        camelContext.addRoutes(camelRoute)
        camelContext.addRoutes(routeBuilder)

        camelContext.start()
        camelContext.createProducerTemplate().sendBody("direct:inputCamel", "toto")
        camelContext.createProducerTemplate().sendBody("direct:input", "toto")

        csuccess must beTrue
        success must beTrue
      }

    }

  }

}

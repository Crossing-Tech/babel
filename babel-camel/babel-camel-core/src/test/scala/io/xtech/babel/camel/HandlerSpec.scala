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
import io.xtech.babel.fish.model.Message
import io.xtech.babel.fish.{ BodyPredicate, MessagePredicate }
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.{ Exchange, LoggingLevel, Processor }
import org.apache.log4j.spi.LoggingEvent
import org.apache.log4j.{ AppenderSkeleton, Level }
import org.slf4j.LoggerFactory
import org.specs2.mutable.SpecificationWithJUnit
import scala.collection.immutable

class HandlerSpec extends SpecificationWithJUnit {

  def validateLog(event: LoggingEvent, loggerName: String) = {
    event.getLevel === Level.TRACE
    event.getLoggerName === loggerName
    event.getMessage.toString.split("\n").headOption.getOrElse(throw new Exception("no logs received")) must beMatching("Failed delivery for (.*). Exhausted after delivery attempt: 1 caught: java.lang.Exception")
  }

  "Error handling at Route level" should {

    "handle exception with onException" in {

      "handle exception with onException (continued : Boolean)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].continuedBody(true).sub("catch").to("mock:catch")
            }.as[String]
            .processBody(x => throw if (x == null) new Exception() else new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockCatch = camelContext.getEndpoint("mock:catch", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockCatch.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])

        mock.assertIsSatisfied()
        mockCatch.assertIsSatisfied()

        camelContext.createProducerTemplate().sendBody("direct:input", null) must (throwA[Exception])

      }

      "handle exception with onException (continued : BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].continuedBody(BodyPredicate((x: Any) => x.toString == "toto"))
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (continued : MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].continued(MessagePredicate((x: Message[Any]) => x.body.get.toString == "toto"))
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (continued : Function on the Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          //#doc:babel-camel-exceptionClause-continued
          from("direct:input")
            .handle {
              route =>
                //Messages which causes IllegalArgumentException
                route.on[IllegalArgumentException].
                  //are catch and continue the flow if body is "toto"
                  continuedBody((x: Any) => x.toString == "toto")
            }
            //#doc:babel-camel-exceptionClause-continued
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (continued : Function on the Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].
                  continued((x: Message[Any]) => x.body.get.toString == "toto")
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : Boolean)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].handledBody(true).sub("exception").to("mock:exception")
            }.as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException()

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()
        mockException.expectedBodiesReceived(ex1)

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].handledBody(BodyPredicate((x: Any) => x.toString.contains("toto")))

            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].handled(MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("toto")))
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : Function on the Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          //#doc:babel-camel-exceptionClause-handled
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].handledBody((x: Any) => x.toString.contains("toto"))

            }
            //#doc:babel-camel-exceptionClause-handled
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : Function on the Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[IllegalArgumentException].handled((x: Message[Any]) => x.body.get.toString.contains("toto"))

            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (when: BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.onBody[Exception](BodyPredicate((x: Any) => x.toString.contains("toto"))).continuedBody(true)
                route.onBody[Exception](BodyPredicate((x: Any) => x.toString.contains("tata"))).handledBody(true)
                  .sub("exception").to("mock:exception")
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockException.expectedBodiesReceived("tata")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "tata") must not(throwA[Exception])
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */
        mock.assertIsSatisfied()
        mockException.assertIsSatisfied()

      }

      "handle exception with onException (when: MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[Exception](MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("toto"))).continuedBody(true)
                route.on[Exception](MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("tata"))).handledBody(true)
                  .sub("exception").to("mock:exception")
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockException.expectedBodiesReceived("tata")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "tata") must not(throwA[Exception])
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */
        mock.assertIsSatisfied()
        mockException.assertIsSatisfied()

      }

      "handle exception with onException (when: Function on the Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          //#doc:babel-camel-exceptionClause-when
          from("direct:input")
            .handle {
              route =>
                //Message containing "toto" and causing an Exception should continue the route
                route.onBody[Exception]((x: Any) => x.toString.contains("toto")).continuedBody(true)
                //Message containing "tata" and causing an Exception should stop and the Exception
                //    should be tagged as handled
                route.onBody[Exception]((x: Any) => x.toString.contains("tata")).handledBody(true)
                  .sub("exception").to("mock:exception")
            }
            //#doc:babel-camel-exceptionClause-when
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockException.expectedBodiesReceived("tata")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "tata") must not(throwA[Exception])
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */
        mock.assertIsSatisfied()
        mockException.assertIsSatisfied()

      }

      "handle exception with onException (when: Function on the Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          from("direct:input")
            .handle {
              route =>
                route.on[Exception]((x: Message[Any]) => x.body.get.toString.contains("toto")).continuedBody(true)
                route.on[Exception]((x: Message[Any]) => x.body.get.toString.contains("tata")).handledBody(true)
                  .sub("exception").to("mock:exception")
            }
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockException.expectedBodiesReceived("tata")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "tata") must not(throwA[Exception])
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */
        mock.assertIsSatisfied()
        mockException.assertIsSatisfied()

      }

      "handle exception with onException with subroute" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {
          //#doc:babel-camel-exceptionClause-2
          from("direct:input")
            .handle {
              //Any message which cases an IllegalArgumentException
              _.on[IllegalArgumentException].
                //should be transfered, via a sub route called "illegal-argument"
                sub("illegal-argument").
                //to the "mock:sub" endpoint (the sub route may also consists into more steps)s
                to("mock:sub")
            }
            //#doc:babel-camel-exceptionClause-2
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        val mockException = camelContext.getEndpoint("mock:sub", classOf[MockEndpoint])

        camelContext.start()

        mock.setExpectedMessageCount(0)
        mockException.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must throwA[Exception]

        mockException.assertIsSatisfied()
        mock.assertIsSatisfied()

      }

    }

    "provide the defaultErrorHandler" in new camel {
      val redeliveries = 2
      val camelProcessor = new Processor {
        var i = redeliveries

        override def process(exchange: Exchange): Unit = {
          i -= 1
          if (i > 0)
            throw new IllegalArgumentException(s"blah:${exchange.getIn.getBody}")
        }
      }

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          from("direct:camel").errorHandler(deadLetterChannel("mock:camel-error").maximumRedeliveries(2)).process(camelProcessor).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(1)

      val routes = new RouteBuilder {

        from("direct:blah").to("mock:blah")
        // to ensure the right route is take into account for error handling

        var i = redeliveries
        //#doc:babel-camel-defaultErrorHandler
        from("direct:babel")
          //The exchange may get redelivered twice before the Exception is raised higher
          .handle(_.defaultErrorHandler.maximumRedeliveries(2))
          //#doc:babel-camel-defaultErrorHandler
          .processBody(any => {
            i -= 1;
            if (i > 0) throw new IllegalArgumentException(s"blah:$any") else any
          })
          .to("mock:success")

        from("direct:halb").to("mock:halb") // to ensure the right route is take into account for error handling

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(1)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "toto")
      proc.sendBody("direct:babel", "toto")

      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()

    }

    "provide the deadletter channel" in new camel {
      val redeliveries = 2
      val camelProcessor = new Processor {
        var i = redeliveries

        override def process(exchange: Exchange): Unit = {
          i -= 1
          if (i > 0)
            throw new IllegalArgumentException(s"blah:${exchange.getIn.getBody}")
        }
      }

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          from("direct:camel").errorHandler(deadLetterChannel("mock:camel-error").maximumRedeliveries(2)).process(camelProcessor).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelError = camelContext.getEndpoint("mock:camel-error").asInstanceOf[MockEndpoint]
      mockCamelError.setExpectedMessageCount(0)
      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(1)

      val routes = new RouteBuilder {

        from("direct:blah").to("mock:blah")
        // to ensure the right route is take into account for error handling

        var i = redeliveries

        //#doc:babel-camel-deadletter
        from("direct:babel")
          //Message causing exception would be sent to the deadletter
          .handle(_.deadletter("mock:error").maximumRedeliveries(2))
          //#doc:babel-camel-deadletter
          .processBody(any => {
            i -= 1
            if (i > 0) throw new IllegalArgumentException(s"blah:$any") else any
          })
          .to("mock:success")

        from("direct:halb").to("mock:halb") // to ensure the right route is take into account for error handling

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockError = camelContext.getEndpoint("mock:error").asInstanceOf[MockEndpoint]
      mockError.setExpectedMessageCount(0)
      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(1)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "toto")
      proc.sendBody("direct:babel", "toto")

      mockError.assertIsSatisfied()
      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()
      mockCamelError.assertIsSatisfied()

    }

    "provide noErrorHandler" in new camel {

      class MyMasterRoutes extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          onException(classOf[IllegalArgumentException]).continued(true)
          //end is used to end a route on onException, not used with continued...
          from("direct:camel").to("direct:b").to("mock:end")
        }
      }

      class MyErrorRoutes extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          //let the parent route manage the error
          //otherwise, exception raises caller
          errorHandler(noErrorHandler())
          from("direct:b").throwException(new IllegalArgumentException("Forced")) to ("mock:toto")
        }
      }

      val mock = camelContext.getEndpoint("mock:end", classOf[MockEndpoint])
      mock.setExpectedMessageCount(1)
      val mockCamelException = camelContext.getEndpoint("mock:toto", classOf[MockEndpoint])
      mockCamelException.setExpectedMessageCount(0)

      camelContext.addRoutes(new MyMasterRoutes())
      camelContext.addRoutes(new MyErrorRoutes())
      camelContext.start()

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "toto - camel") //must throwA[CamelExecutionException]

      mock.assertIsSatisfied()

      //babel version
      val masterRoutes = new RouteBuilder {
        handle(_.on[IllegalArgumentException].continuedBody(true))
        from("direct:babel")
          .to("direct:channel")
          .to("mock:babel")
      }
      val errorRoutes = new RouteBuilder {

        from("direct:channel").handle(_.noErrorHandler)
          .log("before")
          .process(any => throw new IllegalArgumentException(s"blah:$any"))
          .log("after")
          .to("mock:exception")
      }
      camelContext.addRoutes(masterRoutes)
      camelContext.addRoutes(errorRoutes)
      camelContext.start()

      val mockBabel = camelContext.getEndpoint("mock:babel", classOf[MockEndpoint])
      val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])
      mockBabel.setExpectedMessageCount(1)
      mockException.setExpectedMessageCount(0)
      // proc.sendBody("direct:babel", "toto") throwA[NullPointerException]
      proc.sendBody("direct:babel", "toto")
      mockBabel.assertIsSatisfied()
      mockException.assertIsSatisfied()

    }

    "provide loggingErrorHandler" in new camel {

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          from("direct:camel").errorHandler(loggingErrorHandler(LoggerFactory.getLogger("my.cool.tata"), LoggingLevel.TRACE))
            .throwException(new Exception()).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(0)

      val routes = new RouteBuilder {
        //#doc:babel-camel-loggingErrorHandler

        import org.apache.camel.LoggingLevel
        import org.slf4j.LoggerFactory

        from("direct:babel")
          //logs raised Exception at the Trace level
          .handle(_.loggingErrorHandler(level = LoggingLevel.TRACE,
            logger = LoggerFactory.getLogger("my.cool.tata")))

          .processBody(_ => throw new Exception())
          .to("mock:success")
        //#doc:babel-camel-loggingErrorHandler

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(0)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "Expected exception") must throwA[Exception]
      proc.sendBody("direct:babel", "Expected exception") must throwA[Exception]

      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()

      val eventCamel = ErrorSharedLogs.events.headOption.getOrElse(throw new Exception("no log received"))
      validateLog(eventCamel, "my.cool.tata")
      val eventBabel = ErrorSharedLogs.events.tail.headOption.getOrElse(throw new Exception("no log received"))
      validateLog(eventBabel, "my.cool.tata")

    }

  }

  "Error handling at RouteBuilder level" should {

    "handle exception with onException" in {

      "handle exception with onException (continued : Boolean)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].continuedBody(true).sub("catch").to("mock:catch")
          }

          from("direct:input")
            .as[String]
            .processBody(x => throw if (x == null) new Exception() else new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockCatch = camelContext.getEndpoint("mock:catch", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived("toto")
        mockCatch.expectedBodiesReceived("toto")

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])

        mock.assertIsSatisfied()
        mockCatch.assertIsSatisfied()

        mock.reset()

        camelContext.createProducerTemplate().sendBody("direct:input", null) must (throwA[Exception])

      }

      "handle exception with onException (continued : BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].continuedBody(BodyPredicate((x: Any) => {
                x.toString == "toto"
              }))
          }

          from("direct:input")
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.getReceivedExchanges().get(0).getIn.getBody(classOf[String]) === "toto"

      }

      "handle exception with onException (continued : MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].continued(MessagePredicate((x: Message[Any]) => {
                x.body.get.toString == "toto"
              }))
          }

          from("direct:input")
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.getReceivedExchanges().get(0).getIn.getBody(classOf[String]) === "toto"

      }

      "handle exception with onException (continued : Function on the Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].continuedBody((x: Any) => x.toString == "toto")
          }

          from("direct:input")
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.getReceivedExchanges().get(0).getIn.getBody(classOf[String]) === "toto"

      }

      "handle exception with onException (continued : Function on the Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].continued((x: Message[Any]) => {
                x.body.get.toString == "toto"
              })
          }

          from("direct:input")
            .as[String]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must (throwA[Exception])

        mock.getReceivedExchanges().get(0).getIn.getBody(classOf[String]) === "toto"

      }

      "handle exception with onException (handled : Boolean)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].handledBody(true).sub("exception").to("mock:exception")
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException()

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()
        mockException.expectedBodiesReceived(ex1)

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].handledBody(BodyPredicate((x: Any) => x.toString.contains("toto"))).sub("exception").to("mock:exception")
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException("toto")

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", new IllegalArgumentException("Expected exception")) must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].handled(MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("toto"))).sub("exception").to("mock:exception")
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException("toto")

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()
        mockException.expectedBodiesReceived(ex1)

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", new IllegalArgumentException("Expected exception")) must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : Function on the Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].handledBody((x: Any) => x.toString.contains("toto")).sub("exception").to("mock:exception")
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException("toto")

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", new IllegalArgumentException("Expected exception")) must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (handled : Function on the Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[IllegalArgumentException].handled((x: Message[Any]) => x.body.get.toString.contains("toto")).sub("exception").to("mock:exception")
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException("toto")

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])
        val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])

        camelContext.start()

        mock.expectedBodiesReceived()
        mockException.expectedBodiesReceived(ex1)

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", new IllegalArgumentException("Expected exception")) must (throwA[Exception])

        mock.assertIsSatisfied()

      }

      "handle exception with onException (when: BodyPredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.onBody[Exception](BodyPredicate((x: Any) => x.toString.contains("toto"))).continuedBody(true)
              route.onBody[Exception](BodyPredicate((x: Any) => x.toString.contains("Expected exception"))).continuedBody(false)
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must throwA[Exception]
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */

        mock.getReceivedExchanges.get(0).getIn.getBody(classOf[String]) === "toto"
        mock.getReceivedExchanges.size === 1

      }

      "handle exception with onException (when: MessagePredicate)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[Exception](MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("toto"))).continuedBody(true)
              route.on[Exception](MessagePredicate((x: Message[Any]) => x.body.get.toString.contains("Expected exception"))).continuedBody(false)
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must throwA[Exception]

        mock.getReceivedExchanges.get(0).getIn.getBody(classOf[String]) === "toto"
        mock.getReceivedExchanges.size === 1

      }

      "handle exception with onException (when: Function on Body)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.onBody[Exception]((x: Any) => x.toString.contains("toto")).continuedBody(true)
              route.onBody[Exception]((x: Any) => x.toString.contains("Expected exception")).continuedBody(false)
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must throwA[Exception]
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */

        mock.getReceivedExchanges.get(0).getIn.getBody(classOf[String]) === "toto"
        mock.getReceivedExchanges.size === 1

      }

      "handle exception with onException (when: Function on Message)" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            route =>
              route.on[Exception]((x: Message[Any]) => x.body.get.toString.contains("toto")).continuedBody(true)
              route.on[Exception]((x: Message[Any]) => x.body.get.toString.contains("Expected exception")).continuedBody(false)
          }

          from("direct:input")
            .as[Exception]
            .processBody(x => throw new IllegalArgumentException(x))
            .to("mock:output")
        }

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        camelContext.start()

        camelContext.createProducerTemplate().sendBody("direct:input", "toto") must not(throwA[Exception])
        camelContext.createProducerTemplate().sendBody("direct:input", "Expected exception") must throwA[Exception]
        /*WARNING
      following code cause camel to keep an inflight exchange as it causes an exception while processing the predicate (npe)
      val toto = new IllegalArgumentException()
      camelContext.createProducerTemplate().sendBody("direct:input", toto) must (throwA[Exception])
      */

        mock.getReceivedExchanges.get(0).getIn.getBody(classOf[String]) === "toto"
        mock.getReceivedExchanges.size === 1

      }

      "handle exception with onException with subroute" in new camel {

        import io.xtech.babel.camel.builder.RouteBuilder

        val routeBuilder = new RouteBuilder {

          handle {
            _.on[IllegalArgumentException].sub("illegal-argument").to("mock:sub")
          }

          from("direct:input")
            .as[Exception].processBody(x => throw x)
            .to("mock:output")
        }

        val ex1 = new IllegalArgumentException()

        camelContext.addRoutes(routeBuilder)

        val mock = camelContext.getEndpoint("mock:output", classOf[MockEndpoint])

        val mockException = camelContext.getEndpoint("mock:sub", classOf[MockEndpoint])

        camelContext.start()

        mock.setExpectedMessageCount(0)
        mockException.expectedBodiesReceived(ex1)

        camelContext.createProducerTemplate().sendBody("direct:input", ex1) must throwA[Exception]

        mockException.assertIsSatisfied()
        mock.assertIsSatisfied()

      }

    }

    "provide the defaultErrorHandler" in new camel {
      val redeliveries = 2
      val camelProcessor = new Processor {
        var i = redeliveries

        override def process(exchange: Exchange): Unit = {
          i -= 1
          if (i > 0)
            throw new IllegalArgumentException(s"blah:${exchange.getIn.getBody}")
        }
      }

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          errorHandler(defaultErrorHandler().maximumRedeliveries(0).maximumRedeliveries(2))
          from("direct:camel").process(camelProcessor).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(1)

      val routes = new RouteBuilder {

        handle {
          scope =>
            scope.defaultErrorHandler.maximumRedeliveries(2)
        }
        from("direct:blah").to("mock:blah")
        // to ensure the right route is take into account for error handling

        var i = redeliveries
        from("direct:babel")
          .process(any => {
            i -= 1;
            if (i > 0) throw new IllegalArgumentException(s"blah:$any") else any
          })
          .to("mock:success")

        from("direct:halb").to("mock:halb") // to ensure the right route is take into account for error handling

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(1)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "Expected exception")
      proc.sendBody("direct:babel", "Expected exception")

      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()
    }

    "provide the deadletter channel" in new camel {
      val redeliveries = 2
      val camelProcessor = new Processor {
        var i = redeliveries

        override def process(exchange: Exchange): Unit = {
          i -= 1
          if (i > 0)
            throw new IllegalArgumentException(s"blah:${exchange.getIn.getBody}")
        }
      }

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          errorHandler(deadLetterChannel("mock:camel-error").maximumRedeliveries(2))
          from("direct:camel").process(camelProcessor).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelError = camelContext.getEndpoint("mock:camel-error").asInstanceOf[MockEndpoint]
      mockCamelError.setExpectedMessageCount(0)
      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(1)

      val routes = new RouteBuilder {
        //#doc:babel-camel-handleScope
        handle {
          route =>
            //Message raising Exception in any of the following route
            //    will be sent to the deadletter
            route.deadletter("mock:error").maximumRedeliveries(2)
        }
        from("direct:blah").to("mock:blah")
        from("direct:blih").to("mock:blih")
        //#doc:babel-camel-handleScope
        // to ensure the right route is take into account for error handling

        var i = redeliveries
        from("direct:babel")
          .process(any => {
            i -= 1;
            if (i > 0) throw new IllegalArgumentException(s"blah:$any") else any
          })
          .to("mock:success")

        from("direct:halb").to("mock:halb") // to ensure the right route is take into account for error handling

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockError = camelContext.getEndpoint("mock:error").asInstanceOf[MockEndpoint]
      mockError.setExpectedMessageCount(0)
      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(1)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "Expected exception")
      proc.sendBody("direct:babel", "Expected exception")

      mockError.assertIsSatisfied()
      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()
      mockCamelError.assertIsSatisfied()

    }

    "provide noErrorHandler" in new camel {

      class MyMasterRoutes extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          onException(classOf[IllegalArgumentException]).continued(true)
          //end is used to end a route on onException, not used with continued...
          from("direct:camel").to("direct:b").to("mock:end")
        }
      }

      class MyErrorRoutes extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          //let the parent route manage the error
          //otherwise, exception raises caller
          from("direct:b").errorHandler(noErrorHandler()).throwException(new IllegalArgumentException("Forced")) to ("mock:toto")
        }
      }

      val mock = camelContext.getEndpoint("mock:end", classOf[MockEndpoint])
      mock.setExpectedMessageCount(1)
      val mockCamelException = camelContext.getEndpoint("mock:toto", classOf[MockEndpoint])
      mockCamelException.setExpectedMessageCount(0)

      camelContext.addRoutes(new MyMasterRoutes())
      camelContext.addRoutes(new MyErrorRoutes())
      camelContext.start()

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "toto - camel") //must throwA[CamelExecutionException]

      mock.assertIsSatisfied()
      mockCamelException.assertIsSatisfied()

      //babel version
      //#doc:babel-camel-noErrorHandler
      val masterRoutes = new RouteBuilder {
        //#doc:babel-camel-exceptionClause-1
        from("direct:babel")
          .handle(_.on[IllegalArgumentException].continuedBody(true))
          .to("direct:channel").to("mock:babel")
        //#doc:babel-camel-exceptionClause-1
      }
      val errorRoutes = new RouteBuilder {
        from("direct:channel")
          //erase the error handling policy defined in the higher route
          .handle(_.noErrorHandler)
          .process(any => throw new IllegalArgumentException(s"blah:$any")).
          to("mock:exception")
      }
      //#doc:babel-camel-noErrorHandler
      camelContext.addRoutes(masterRoutes)
      camelContext.addRoutes(errorRoutes)
      camelContext.start()

      val mockBabel = camelContext.getEndpoint("mock:babel", classOf[MockEndpoint])

      val mockException = camelContext.getEndpoint("mock:exception", classOf[MockEndpoint])
      mockBabel.setExpectedMessageCount(1)
      mockException.setExpectedMessageCount(0)
      // proc.sendBody("direct:babel", "Expected exception") throwA[NullPointerException]
      proc.sendBody("direct:babel", "Expected exception")
      mockBabel.assertIsSatisfied()
      mockException.assertIsSatisfied()

    }

    "provide loggingErrorHandler" in new camel {

      class CamelRoute extends org.apache.camel.builder.RouteBuilder {
        def configure(): Unit = {
          errorHandler(loggingErrorHandler(LoggerFactory.getLogger("my.cool.titi"), LoggingLevel.TRACE))

          from("direct:camel")
            .throwException(new Exception()).to("mock:camel-success")
        }
      }

      camelContext.addRoutes(new CamelRoute())

      val mockCamelSuccess = camelContext.getEndpoint("mock:camel-success").asInstanceOf[MockEndpoint]
      mockCamelSuccess.setExpectedMessageCount(0)

      val routes = new RouteBuilder {

        handle(_.loggingErrorHandler(level = LoggingLevel.TRACE, logger = LoggerFactory.getLogger("my.cool.titi")))
        from("direct:babel")
          .processBody(_ => throw new Exception())
          .to("mock:success")

      }
      camelContext.addRoutes(routes)
      camelContext.start()

      val mockSuccess = camelContext.getEndpoint("mock:success").asInstanceOf[MockEndpoint]
      mockSuccess.setExpectedMessageCount(0)

      val proc = camelContext.createProducerTemplate()
      proc.sendBody("direct:camel", "Expected exception") must throwA[Exception]
      proc.sendBody("direct:babel", "Expected exception") must throwA[Exception]

      mockSuccess.assertIsSatisfied()
      mockCamelSuccess.assertIsSatisfied()

      val eventCamel = RBErrorSharedLogs.events.headOption.getOrElse(throw new Exception("no log received"))
      validateLog(eventCamel, "my.cool.titi")
      val eventBabel = RBErrorSharedLogs.events.tail.headOption.getOrElse(throw new Exception("no log received"))
      validateLog(eventBabel, "my.cool.titi")

    }

    "throw an exception if handle is called twice" in new camel {

      val routes = new RouteBuilder {

        handle {
          scope =>
            scope.deadletter("mock:error")
        }
        handle {
          scope =>
            scope.deadletter("mock:other")
        }
        from("direct:blah").to("mock:blah")
        // to ensure the right route is take into account for error handling

      } must throwA[CamelException.ErorrHandlingDefinedTwice]

    }

  }

}

object ErrorSharedLogs {
  var events: immutable.Seq[LoggingEvent] = List.empty[LoggingEvent]
}

class ErrorAppender extends AppenderSkeleton {

  def append(event: LoggingEvent): Unit = {
    ErrorSharedLogs.events :+= event
  }

  def close(): Unit = {}

  def requiresLayout(): Boolean = false
}

object RBErrorSharedLogs {
  var events: immutable.Seq[LoggingEvent] = List.empty[LoggingEvent]
}

class RBErrorAppender extends AppenderSkeleton {

  def append(event: LoggingEvent): Unit = {
    RBErrorSharedLogs.events :+= event
  }

  def close(): Unit = {}

  def requiresLayout(): Boolean = false
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.Test._
import io.xtech.babel.fish.model._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecificationWithJUnit

class DSLSpec extends SpecificationWithJUnit {
  sequential

  "DSL" should {

    "create a route definition with a simple route (form,processBody,process,endpoint)" in {

      import io.xtech.babel.fish.Test._

      // create a route
      val definitions = new DSL {
        from("direct:input").processBody((s: String) => s.toInt).process(msg => msg.withBody(_.toString)).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.headOption.map(_.from.source.uri) mustEqual Some("direct:input")

      val bodyProcStep = for {
        s <- definitions.headOption
        step <- s.from.next
      } yield step

      bodyProcStep must beSome.like[MatchResult[Any]] {
        case step: TransformerDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val msgProcStep = for { previous <- bodyProcStep; step <- previous.next } yield step
      msgProcStep must beSome.like[MatchResult[Any]] {
        case step: TransformerDefinition[_, _] => {
          step.expression must haveClass[MessageTransformationExpression[_, _]]
        }
      }

      val endpointStep = for { previous <- msgProcStep; step <- previous.next } yield step
      endpointStep must beSome.like[MatchResult[Any]] {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }

      // the last node must be None and not null
      endpointStep must beSome.which(endpoint => endpoint.next must beNone)
    }

    "create a route definition with a route containing a choice (router)" in {
      import io.xtech.babel.fish.Test._

      // create a route
      val definitions = new DSL() {
        from("direct:input").as[String].choice {
          c =>
            c.when(_.body == "1").to("mock:output1")
            c.when(_.body == "2").to("mock:output2")
            c.otherwise.to("mock:output3")
        }.to("mock:output4")
      }.build()

      // tests the definition generated from the DSL
      definitions.headOption.map(_.from.source.uri) mustEqual Some("direct:input")

      val bodyConvStep = for {
        s <- definitions.headOption
        step <- s.from.next
      } yield step
      bodyConvStep must beSome.like[MatchResult[Any]] {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[String]
        }
      }

      val choiceStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      choiceStep must beSome.like[MatchResult[Any]] {
        case choice: ChoiceDefinition[_] => {

          choice.scopedSteps(0).next must beSome.like {
            case endpoint: EndpointDefinition[_, _] => {
              endpoint.sink.uri mustEqual "mock:output1"
            }
          }

          choice.scopedSteps(1).next must beSome.like {
            case endpoint: EndpointDefinition[_, _] => {
              endpoint.sink.uri mustEqual "mock:output2"
            }
          }

          choice.otherwise must beSome

          val endpoint = for (previous <- choice.otherwise; step <- previous.next) yield step
          endpoint must beSome.like[MatchResult[Any]] {
            case endpoint: EndpointDefinition[_, _] => {
              endpoint.sink.uri mustEqual "mock:output3"
            }
          }
        }
      }

      val endpointStep = for { previous <- choiceStep; step <- previous.next } yield step
      endpointStep must beSome.like[MatchResult[Any]] {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output4"
        }
      }
    }

    "create a route definition with a route containing a bodiesplitter and filter" in {
      import io.xtech.babel.fish.Test._

      // create a route
      val definitions = new DSL() {
        from("direct:input").as[String].splitBody(_.split(",").toIterator).filter(_.body.exists(_ == "bla")).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.headOption.map(_.from.source.uri) mustEqual Some("direct:input")

      val bodyConvStep = for {
        s <- definitions.headOption
        step <- s.from.next
      } yield step
      bodyConvStep must beSome.like[MatchResult[Any]] {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[String]
        }
      }

      val bodiesplitterStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      bodiesplitterStep must beSome.like[MatchResult[Any]] {
        case step: SplitterDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val msgFilterStep = for { previous <- bodiesplitterStep; step <- previous.next } yield step
      msgFilterStep must beSome.like[MatchResult[Any]] {
        case step: FilterDefinition[_] => {
          step.predicate must haveClass[MessagePredicate[_]]
        }
      }

      val endpointStep = for { previous <- msgFilterStep; step <- previous.next } yield step
      endpointStep must beSome.like[MatchResult[Any]] {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }

      // the last node must be None and not null
      endpointStep must beSome.which(endpoint => endpoint.next must beNone)
    }

    "create a route definition with a route containing a mulitcast" in {
      import io.xtech.babel.fish.Test._

      // create a route
      val definitions = new DSL() {
        from("direct:input").as[String].multicast("mock:output1", "mock:output2", "mock:output3")
      }.build()

      // tests the definition generated from the DSL
      definitions.headOption.map(_.from.source.uri) mustEqual Some("direct:input")

      val bodyConvStep = for {
        s <- definitions.headOption
        step <- s.from.next
      } yield step
      bodyConvStep must beSome.like[MatchResult[Any]] {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[String]
        }
      }

      val multicastStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      multicastStep must beSome.like[MatchResult[Any]] {
        case step: MulticastDefinition[_] => {

          step.sinks.seq.map(_.uri) mustEqual Seq("mock:output1", "mock:output2", "mock:output3")
        }
      }

      // the last node must be None and not null
      multicastStep must beSome.which(multicast => multicast.next must beNone)
    }

    "throw a Validation exception when nothing in the DSL" in {
      new DSL {}.build() must throwA[RouteDefinitionException].like {
        case e: RouteDefinitionException => {
          e.getMessage mustEqual "The route has validation errors"
          e.errors must contain(ValidationError("No routes defined in the DSL", None))
          e.toString() mustEqual "io.xtech.babel.fish.RouteDefinitionException: The route has validation errors (No routes defined in the DSL)"
        }
      }
    }

    "throw a Validation exception when a sequence of definition ends with a FromDefintion" in {
      val definition = FromDefinition(classOf[Any], "direct:toto")
      val evil = FromDefinition(classOf[Any], "direct:evil")
      definition.next = Some(evil)
      definition.validate() must beLike {
        case List(validation: ValidationError, second: ValidationError) =>
          validation.errorMessage must contain("may not end using a from")
          second.errorMessage must contain("from may only start a route")
          validation.definition === Some(evil)
      }
    }

    "throw a Validation exception when a Definition has for next step a FromDefintion" in {
      val definition = FromDefinition(classOf[Any], "direct:toto")
      val second = FromDefinition(classOf[Any], "direct:evil")
      definition.next = Some(second)
      val third = EndpointDefinition("direct:tata")
      second.next = Some(third)
      definition.validate() must beLike {
        case List(validation: ValidationError) =>
          validation.errorMessage must contain("from may only start a route")
          validation.definition === Some(second)
      }
    }

    //because might be inout, there is no validation on last step of a route
    "not throw a Validation exception when a route finishing with a filter" in {
      import io.xtech.babel.fish.Test._
      val routeDef = new DSL {
        from("direct:input").as[String].filter(_.body.exists(_ == "false"))
      }

      routeDef.build should not(throwA[RouteDefinitionException])

    }
  }
}

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecificationWithJUnit

class AggregateDSLSpec extends SpecificationWithJUnit {
  sequential

  "aggregate keyword" should {
    "create a route definition with a route containing an aggregation" in {
      import io.xtech.babel.fish.Test._

      case class AP[I, O](func: (I => O)) extends AggregationConfiguration[I, O]

      // create a route
      val definitions = new DSL() {
        from("direct:input").as[String].aggregate(AP((s: String) => "s")).to("mock:output")
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

      val aggregatorStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      aggregatorStep must beSome.like[MatchResult[Any]] {
        case step: AggregationDefinition[_, _] =>
          step.configuration must haveClass[AP[String, String]]
      }

      val endpointStep = for { previous <- aggregatorStep; step <- previous.next } yield step
      endpointStep must beSome.like[MatchResult[Any]] {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }
    }
  }
}

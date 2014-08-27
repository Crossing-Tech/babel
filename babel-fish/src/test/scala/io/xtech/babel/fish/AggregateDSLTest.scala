/*
 *
 *    ___                      _   _     _ _          ___        _
 *   / __|___ _ _  _ _  ___ __| |_(_)_ _(_) |_ _  _  | __|_ _ __| |_ ___ _ _ _  _  TM
 *  | (__/ _ \ ' \| ' \/ -_) _|  _| \ V / |  _| || | | _/ _` / _|  _/ _ \ '_| || |
 *   \___\___/_||_|_||_\___\__|\__|_|\_/|_|\__|\_, | |_|\__,_\__|\__\___/_|  \_, |
 *                                             |__/                          |__/
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.fish

import io.xtech.babel.fish.model._
import org.specs2.mutable.SpecificationWithJUnit

class AggregateDSLTest extends SpecificationWithJUnit {
  sequential

  "aggregate keyword" should {
    "create a route definition with a route containing an aggregation" in {
      import Test._

      case class AP[I, O](func: (I => O)) extends AggregationConfiguration[I, O]

      // create a route
      val definitions = new DSL() {
        from("direct:input").as[String].aggregate(AP((s: String) => "s")).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.head.from.source.uri mustEqual "direct:input"

      val bodyConvStep = for { step <- definitions.head.from.next } yield step
      bodyConvStep must beSome.like {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[String]
        }
      }

      val aggregatorStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      aggregatorStep must beSome.like {
        case step: AggregationDefinition[_, _] =>
          step.configuration must haveClass[AP[String, String]]
      }

      val endpointStep = for { previous <- aggregatorStep; step <- previous.next } yield step
      endpointStep must beSome.like {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }
    }
  }
}

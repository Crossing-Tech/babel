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

class SplitterDSLTest extends SpecificationWithJUnit {

  "splitBody keyword" should {

    "accept a function" in {

      import Test._

      val definitions = new DSL {
        from("direct:input").as[List[String]].splitBody(_.iterator).processBody((str: String) => str.toInt).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.head.from.source.uri mustEqual "direct:input"

      val bodyConvStep = for { step <- definitions.head.from.next } yield step
      bodyConvStep must beSome.like {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[List[String]]
        }
      }

      val splitterStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      splitterStep must beSome.like {
        case step: SplitterDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val bodyProcStep = for { previous <- splitterStep; step <- previous.next } yield step
      bodyProcStep must beSome.like {
        case step: TransformerDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val endpointStep = for { previous <- bodyProcStep; step <- previous.next } yield step
      endpointStep must beSome.like {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }

    }
  }

  "split keyword" should {

    "accept a function" in {

      import io.xtech.babel.fish.Test._

      val definitions = new DSL {
        from("direct:input").as[List[String]].split(_.body.getOrElse(List.empty).iterator).processBody((str: String) => str.toInt).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.head.from.source.uri mustEqual "direct:input"

      val bodyConvStep = for { step <- definitions.head.from.next } yield step
      bodyConvStep must beSome.like {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[List[String]]
        }
      }

      val splitterStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      splitterStep must beSome.like {
        case step: SplitterDefinition[_, _] => {
          step.expression must haveClass[MessageExpression[_, _]]
        }
      }

      val bodyProcStep = for { previous <- splitterStep; step <- previous.next } yield step
      bodyProcStep must beSome.like {
        case step: TransformerDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val endpointStep = for { previous <- bodyProcStep; step <- previous.next } yield step
      endpointStep must beSome.like {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }

    }

    "accept an expression" in {

      case class TestSplitterExpression() extends Expression[Any, Any]

      import io.xtech.babel.fish.Test._

      val definitions = new DSL {
        from("direct:input").as[List[String]].split(TestSplitterExpression()).processBody(_.toString.toInt).to("mock:output")
      }.build()

      // tests the definition generated from the DSL
      definitions.head.from.source.uri mustEqual "direct:input"

      val bodyConvStep = for { step <- definitions.head.from.next } yield step
      bodyConvStep must beSome.like {
        case step: BodyConvertorDefinition[_, _] => {
          step.outClass mustEqual classOf[List[String]]
        }
      }

      val splitterStep = for { previous <- bodyConvStep; step <- previous.next } yield step
      splitterStep must beSome.like {
        case step: SplitterDefinition[_, _] => {
          step.expression must haveClass[TestSplitterExpression]
        }
      }

      val bodyProcStep = for { previous <- splitterStep; step <- previous.next } yield step
      bodyProcStep must beSome.like {
        case step: TransformerDefinition[_, _] => {
          step.expression must haveClass[BodyExpression[_, _]]
        }
      }

      val endpointStep = for { previous <- bodyProcStep; step <- previous.next } yield step
      endpointStep must beSome.like {
        case step: EndpointDefinition[_, _] => {
          step.sink.uri mustEqual "mock:output"
        }
      }

    }
  }

}

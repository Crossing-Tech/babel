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

import io.xtech.babel.camel._
import io.xtech.babel.camel.builder.RouteBuilderTest
import io.xtech.babel.camel.choice.{ ComplexChoiceTest, SimpleChoiceTest }
import io.xtech.babel.camel.sample.{ SamplePhilosophyTest, SampleTest }
import io.xtech.babel.camel.splitfilter.SimpleSplitFilterTest
import io.xtech.sparta.test.specs2.{ AggregatingSpecification, FormatedSpecification }

import scala.collection.immutable

class BabelCamelSpec extends AggregatingSpecification with FormatedSpecification {

  val title = "Babel Camel Specification"

  val specs = immutable.Seq(new CamelDSLTest, new CamelMessageTest, new CompilationTest,
    new Basics, new Routing, new Transformation, new ErrorHandling, new Deploy, new Integration, new Sample
  )

}

class Basics extends AggregatingSpecification {
  val title = "Babel Camel Routing"
  val specs = immutable.Seq(new RouteIdTest, new AsTest, new RequireAsTest, new LogTest)
}

class Routing extends AggregatingSpecification {
  val title = "Babel Camel Routing"
  val specs = immutable.Seq(new AggregateTest, new SimpleChoiceTest, new MulticastTest, new SubRouteTest)
}

class Transformation extends AggregatingSpecification {
  val title = "Babel Camel Transformation"
  val specs = immutable.Seq(new EnricherTest, new MarshallerTest, new ResequencerTest, new SortTest, new TransformerTest, new XMLTest)
}

class ErrorHandling extends AggregatingSpecification {
  val title = "Babel Camel Error management"
  val specs = immutable.Seq(new HandlerTest, new TransactionTest)
}

class Deploy extends AggregatingSpecification {
  val title = "Babel Camel Deployement"
  val specs = immutable.Seq(new RouteBuilderTest)
}

class Integration extends AggregatingSpecification {
  val title = "Babel Camel Integration"
  val specs = immutable.Seq(new ComplexChoiceTest, new SimpleSplitFilterTest)
}

class Sample extends AggregatingSpecification {
  val title = "Babel Camel Samples"
  val specs = immutable.Seq(new SampleTest, new SamplePhilosophyTest)
}
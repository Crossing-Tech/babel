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

import io.xtech.babel.fish.{ DSLTest, AggregateDSLTest, SplitterDSLTest }
import io.xtech.sparta.test.specs2.{ FormatedSpecification, AggregatingSpecification }
import org.specs2.mutable.Specification
import scala.collection.immutable

class BabelFishSpec extends Specification with AggregatingSpecification with FormatedSpecification {

  val specs = immutable.Seq(new DSLTest, new SplitterDSLTest, new AggregateDSLTest)

  def title = "Babel Fish Specifications"

}
